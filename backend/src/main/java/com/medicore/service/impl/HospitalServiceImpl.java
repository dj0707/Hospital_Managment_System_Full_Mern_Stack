package com.medicore.service.impl;

import com.medicore.dto.HospitalDtos;
import com.medicore.entity.*;
import com.medicore.exception.BadRequestException;
import com.medicore.exception.ConflictException;
import com.medicore.exception.ResourceNotFoundException;
import com.medicore.repository.*;
import com.medicore.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalServiceImpl implements HospitalService {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuditLogRepository auditLogRepository;

    @Value("${app.ai.api-key:}")
    private String aiApiKey;

    @Override
    @Transactional
    public HospitalDtos.WardDto createWard(HospitalDtos.WardDto dto) {
        if (wardRepository.findByName(dto.getName()).isPresent()) {
            throw new ConflictException("Ward with name " + dto.getName() + " already exists");
        }
        Ward ward = Ward.builder()
                .name(dto.getName())
                .wardType(dto.getWardType())
                .dailyRate(dto.getDailyRate())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        Ward saved = wardRepository.save(ward);
        return mapToWardDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalDtos.WardDto> getAllWards() {
        return wardRepository.findAll().stream().map(this::mapToWardDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HospitalDtos.BedDto createBed(HospitalDtos.BedDto dto) {
        if (bedRepository.findByBedNumber(dto.getBedNumber()).isPresent()) {
            throw new ConflictException("Bed number " + dto.getBedNumber() + " already exists");
        }
        Ward ward = wardRepository.findById(dto.getWardId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found with id: " + dto.getWardId()));

        Bed bed = Bed.builder()
                .bedNumber(dto.getBedNumber())
                .ward(ward)
                .status("AVAILABLE")
                .build();
        Bed saved = bedRepository.save(bed);
        return mapToBedDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalDtos.BedDto> getBedsByWard(Long wardId) {
        return bedRepository.findByWardId(wardId).stream().map(this::mapToBedDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalDtos.BedDto> getAvailableBeds() {
        return bedRepository.findByStatus("AVAILABLE").stream().map(this::mapToBedDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HospitalDtos.AdmissionDto admitPatient(HospitalDtos.AdmissionDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        Bed bed = bedRepository.findById(dto.getBedId())
                .orElseThrow(() -> new ResourceNotFoundException("Bed not found with id: " + dto.getBedId()));

        if (!"AVAILABLE".equalsIgnoreCase(bed.getStatus()) || admissionRepository.existsActiveAdmissionForBed(bed.getId())) {
            throw new ConflictException("Bed " + bed.getBedNumber() + " is currently occupied or unavailable");
        }

        bed.setStatus("OCCUPIED");
        bedRepository.save(bed);

        String admCode = "ADM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Admission admission = Admission.builder()
                .admissionCode(admCode)
                .patient(patient)
                .bed(bed)
                .doctor(doctor)
                .admissionDate(LocalDateTime.now())
                .diagnosis(dto.getDiagnosis())
                .status("ADMITTED")
                .build();

        return mapToAdmissionDto(admissionRepository.save(admission));
    }

    @Override
    @Transactional
    public HospitalDtos.AdmissionDto dischargePatient(Long admissionId) {
        Admission adm = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Admission not found with id: " + admissionId));

        if (!"ADMITTED".equalsIgnoreCase(adm.getStatus())) {
            throw new BadRequestException("Patient is already discharged");
        }

        LocalDateTime dischargeTime = LocalDateTime.now();
        adm.setDischargeDate(dischargeTime);
        adm.setStatus("DISCHARGED");

        // Calculate stay charge based on days * ward daily rate
        long hours = Duration.between(adm.getAdmissionDate(), dischargeTime).toHours();
        long days = Math.max(1, (hours + 23) / 24); // minimum 1 day
        BigDecimal dailyRate = adm.getBed().getWard().getDailyRate();
        BigDecimal totalBedCharge = dailyRate.multiply(BigDecimal.valueOf(days));
        adm.setTotalBedCharge(totalBedCharge);

        Bed bed = adm.getBed();
        bed.setStatus("AVAILABLE");
        bedRepository.save(bed);

        return mapToAdmissionDto(admissionRepository.save(adm));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalDtos.AdmissionDto> getActiveAdmissions() {
        return admissionRepository.findCurrentActiveAdmissions().stream()
                .map(this::mapToAdmissionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HospitalDtos.DashboardStatsDto getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        HospitalDtos.DashboardStatsDto stats = new HospitalDtos.DashboardStatsDto();
        stats.setTotalPatients(patientRepository.count());
        stats.setActiveDoctors(doctorRepository.countByIsActiveTrue());
        stats.setTodayAppointments(appointmentRepository.countByAppointmentDate(today));
        stats.setActiveAdmissions(admissionRepository.countByStatus("ADMITTED"));
        stats.setAvailableBeds(bedRepository.countByStatus("AVAILABLE"));
        stats.setTotalMedicines(medicineRepository.countByIsActiveTrue());
        stats.setLowStockMedicines(batchRepository.findNearExpiryBatches(today.plusDays(30)).size());
        stats.setTodaySales(invoiceRepository.sumTotalSalesBetween(startOfDay, endOfDay));
        stats.setMonthlySales(invoiceRepository.sumTotalSalesBetween(startOfMonth, endOfDay));
        stats.setOutstandingBalance(invoiceRepository.sumOutstandingBalance());

        return stats;
    }

    @Override
    public HospitalDtos.AiAssistantResponse askAiAssistant(HospitalDtos.AiAssistantRequest request) {
        HospitalDtos.AiAssistantResponse response = new HospitalDtos.AiAssistantResponse();
        response.setDisclaimer("AI generated insights are advisory and administrative only. Review all clinical decisions with qualified medical practitioners.");

        if (aiApiKey == null || aiApiKey.trim().isEmpty() || aiApiKey.equalsIgnoreCase("your_ai_key_here")) {
            response.setConfigured(false);
            response.setAnswer("AI Operations Assistant is currently not configured with an API key. " +
                    "To enable live AI insights, configure AI_API_KEY in your backend environment variables. " +
                    "MediCore HMS continues to operate in full local deterministic mode for all clinical, pharmacy, and billing operations.");
            return response;
        }

        response.setConfigured(true);
        // Deterministic domain guidance assistant
        String prompt = request.getPrompt().toLowerCase();
        if (prompt.contains("fefo") || prompt.contains("batch") || prompt.contains("stock")) {
            response.setAnswer("In MediCore HMS, the Pharmacy subsystem follows First-Expiry-First-Out (FEFO) dispensing. " +
                    "When dispensing medicines during POS checkout, the system automatically allocates inventory from the batch nearest to expiry, " +
                    "preventing expired stock dispensing and updating the stock ledger atomically.");
        } else if (prompt.contains("appointment") || prompt.contains("conflict") || prompt.contains("booking")) {
            response.setAnswer("Appointment scheduling validates both doctor and patient availability. " +
                    "The backend enforces time slot uniqueness with database locking to reject concurrent double bookings.");
        } else if (prompt.contains("billing") || prompt.contains("invoice") || prompt.contains("strip")) {
            response.setAnswer("Pharmacy billing strictly calculates totals on the Java backend using BigDecimal financial precision. " +
                    "Strip-to-base unit conversion occurs dynamically based on the medicine's unitsPerStrip configuration.");
        } else {
            response.setAnswer("MediCore AI Operations Assistant is active. You can ask questions about hospital workflows, FEFO pharmacy inventory, bed admissions, or appointment scheduling.");
        }
        return response;
    }

    private HospitalDtos.WardDto mapToWardDto(Ward w) {
        HospitalDtos.WardDto dto = new HospitalDtos.WardDto();
        dto.setId(w.getId());
        dto.setName(w.getName());
        dto.setWardType(w.getWardType());
        dto.setDailyRate(w.getDailyRate());
        dto.setIsActive(w.getIsActive());
        return dto;
    }

    private HospitalDtos.BedDto mapToBedDto(Bed b) {
        HospitalDtos.BedDto dto = new HospitalDtos.BedDto();
        dto.setId(b.getId());
        dto.setBedNumber(b.getBedNumber());
        dto.setWardId(b.getWard().getId());
        dto.setWardName(b.getWard().getName());
        dto.setWardType(b.getWard().getWardType());
        dto.setDailyRate(b.getWard().getDailyRate());
        dto.setStatus(b.getStatus());
        return dto;
    }

    private HospitalDtos.AdmissionDto mapToAdmissionDto(Admission a) {
        HospitalDtos.AdmissionDto dto = new HospitalDtos.AdmissionDto();
        dto.setId(a.getId());
        dto.setAdmissionCode(a.getAdmissionCode());
        dto.setPatientId(a.getPatient().getId());
        dto.setPatientName(a.getPatient().getFullName());
        dto.setBedId(a.getBed().getId());
        dto.setBedNumber(a.getBed().getBedNumber());
        dto.setWardName(a.getBed().getWard().getName());
        dto.setDoctorId(a.getDoctor().getId());
        dto.setDoctorName(a.getDoctor().getFullName());
        dto.setAdmissionDate(a.getAdmissionDate());
        dto.setDischargeDate(a.getDischargeDate());
        dto.setDiagnosis(a.getDiagnosis());
        dto.setStatus(a.getStatus());
        dto.setTotalBedCharge(a.getTotalBedCharge());
        return dto;
    }
}
