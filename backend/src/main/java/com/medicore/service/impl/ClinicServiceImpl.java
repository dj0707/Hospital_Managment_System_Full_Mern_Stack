package com.medicore.service.impl;

import com.medicore.dto.ClinicDtos;
import com.medicore.entity.*;
import com.medicore.exception.ConflictException;
import com.medicore.exception.ResourceNotFoundException;
import com.medicore.repository.*;
import com.medicore.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final EncounterRepository encounterRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public ClinicDtos.PatientDto createPatient(ClinicDtos.PatientDto dto) {
        if (patientRepository.existsByPhone(dto.getPhone())) {
            throw new ConflictException("Patient with phone " + dto.getPhone() + " already exists");
        }

        String patientCode = "PAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dob(dto.getDob())
                .gender(dto.getGender())
                .bloodGroup(dto.getBloodGroup())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .emergencyContactName(dto.getEmergencyContactName())
                .emergencyContactPhone(dto.getEmergencyContactPhone())
                .allergies(dto.getAllergies())
                .medicalHistory(dto.getMedicalHistory())
                .build();

        Patient saved = patientRepository.save(patient);
        return mapToPatientDto(saved);
    }

    @Override
    @Transactional
    public ClinicDtos.PatientDto updatePatient(Long id, ClinicDtos.PatientDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDob(dto.getDob());
        patient.setGender(dto.getGender());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setAddress(dto.getAddress());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setAllergies(dto.getAllergies());
        patient.setMedicalHistory(dto.getMedicalHistory());

        return mapToPatientDto(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicDtos.PatientDto getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(this::mapToPatientDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicDtos.PatientDto getPatientByCode(String code) {
        return patientRepository.findByPatientCode(code)
                .map(this::mapToPatientDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with code: " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicDtos.PatientDto> searchPatients(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return patientRepository.findAll(pageable).map(this::mapToPatientDto);
        }
        return patientRepository.searchPatients(query, pageable).map(this::mapToPatientDto);
    }

    @Override
    @Transactional
    public ClinicDtos.DepartmentDto createDepartment(ClinicDtos.DepartmentDto dto) {
        Department dept = Department.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .headOfDept(dto.getHeadOfDept())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
        Department saved = departmentRepository.save(dept);
        return mapToDeptDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicDtos.DepartmentDto> getActiveDepartments() {
        return departmentRepository.findByIsActiveTrue().stream()
                .map(this::mapToDeptDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClinicDtos.DoctorDto createDoctor(ClinicDtos.DoctorDto dto) {
        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));

        String docCode = "DOC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Doctor doctor = Doctor.builder()
                .department(dept)
                .doctorCode(docCode)
                .fullName(dto.getFullName())
                .specialization(dto.getSpecialization())
                .qualification(dto.getQualification())
                .experienceYears(dto.getExperienceYears() != null ? dto.getExperienceYears() : 0)
                .consultationFee(dto.getConsultationFee())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .availableDays(dto.getAvailableDays() != null ? dto.getAvailableDays() : "Mon,Tue,Wed,Thu,Fri,Sat")
                .shiftStart(dto.getShiftStart() != null ? dto.getShiftStart() : "09:00")
                .shiftEnd(dto.getShiftEnd() != null ? dto.getShiftEnd() : "17:00")
                .isActive(true)
                .build();

        return mapToDoctorDto(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public ClinicDtos.DoctorDto updateDoctor(Long id, ClinicDtos.DoctorDto dto) {
        Doctor doc = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            doc.setDepartment(dept);
        }

        doc.setFullName(dto.getFullName());
        doc.setSpecialization(dto.getSpecialization());
        doc.setQualification(dto.getQualification());
        doc.setExperienceYears(dto.getExperienceYears());
        doc.setConsultationFee(dto.getConsultationFee());
        doc.setPhone(dto.getPhone());
        doc.setEmail(dto.getEmail());
        if (dto.getAvailableDays() != null) doc.setAvailableDays(dto.getAvailableDays());
        if (dto.getShiftStart() != null) doc.setShiftStart(dto.getShiftStart());
        if (dto.getShiftEnd() != null) doc.setShiftEnd(dto.getShiftEnd());
        if (dto.getIsActive() != null) doc.setIsActive(dto.getIsActive());

        return mapToDoctorDto(doctorRepository.save(doc));
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicDtos.DoctorDto getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(this::mapToDoctorDto)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicDtos.DoctorDto> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdAndIsActiveTrue(departmentId).stream()
                .map(this::mapToDoctorDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicDtos.DoctorDto> searchDoctors(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return doctorRepository.findAll(pageable).map(this::mapToDoctorDto);
        }
        return doctorRepository.searchDoctors(query, pageable).map(this::mapToDoctorDto);
    }

    @Override
    @Transactional
    public ClinicDtos.AppointmentDto bookAppointment(ClinicDtos.AppointmentDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        // Concurrency / Overlap validation
        if (appointmentRepository.existsDoctorConflict(doctor.getId(), dto.getAppointmentDate(), dto.getAppointmentTime())) {
            throw new ConflictException("Doctor " + doctor.getFullName() + " already has an appointment at " + dto.getAppointmentTime() + " on " + dto.getAppointmentDate());
        }

        if (appointmentRepository.existsPatientConflict(patient.getId(), dto.getAppointmentDate(), dto.getAppointmentTime())) {
            throw new ConflictException("Patient already has an active appointment at this time slot");
        }

        String apptCode = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Appointment appointment = Appointment.builder()
                .appointmentCode(apptCode)
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(dto.getAppointmentDate())
                .appointmentTime(dto.getAppointmentTime())
                .status("SCHEDULED")
                .reason(dto.getReason())
                .consultationFee(doctor.getConsultationFee())
                .build();

        return mapToApptDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public ClinicDtos.AppointmentDto updateAppointmentStatus(Long id, String status) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        appt.setStatus(status.toUpperCase());
        return mapToApptDto(appointmentRepository.save(appt));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClinicDtos.AppointmentDto> filterAppointments(Long doctorId, Long patientId, String status, LocalDate date, Pageable pageable) {
        return appointmentRepository.filterAppointments(doctorId, patientId, status, date, pageable).map(this::mapToApptDto);
    }

    @Override
    @Transactional
    public ClinicDtos.EncounterDto createEncounter(ClinicDtos.EncounterDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        Appointment appt = null;
        if (dto.getAppointmentId() != null) {
            appt = appointmentRepository.findById(dto.getAppointmentId()).orElse(null);
            if (appt != null) {
                appt.setStatus("COMPLETED");
                appointmentRepository.save(appt);
            }
        }

        String code = "ENC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Encounter encounter = Encounter.builder()
                .encounterCode(code)
                .patient(patient)
                .doctor(doctor)
                .appointment(appt)
                .chiefComplaint(dto.getChiefComplaint())
                .vitalsBp(dto.getVitalsBp())
                .vitalsPulse(dto.getVitalsPulse())
                .vitalsTemp(dto.getVitalsTemp())
                .vitalsWeight(dto.getVitalsWeight())
                .vitalsSpo2(dto.getVitalsSpo2())
                .diagnosis(dto.getDiagnosis())
                .clinicalNotes(dto.getClinicalNotes())
                .treatmentPlan(dto.getTreatmentPlan())
                .build();

        return mapToEncounterDto(encounterRepository.save(encounter));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicDtos.EncounterDto> getEncountersByPatient(Long patientId) {
        return encounterRepository.findByPatientIdOrderByEncounterDateDesc(patientId).stream()
                .map(this::mapToEncounterDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClinicDtos.PrescriptionDto createPrescription(ClinicDtos.PrescriptionDto dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + dto.getPatientId()));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + dto.getDoctorId()));

        Encounter encounter = null;
        if (dto.getEncounterId() != null) {
            encounter = encounterRepository.findById(dto.getEncounterId()).orElse(null);
        }

        String prescCode = "RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Prescription prescription = Prescription.builder()
                .prescriptionCode(prescCode)
                .patient(patient)
                .doctor(doctor)
                .encounter(encounter)
                .notes(dto.getNotes())
                .build();

        for (ClinicDtos.PrescriptionItemDto itemDto : dto.getItems()) {
            Medicine med = medicineRepository.findById(itemDto.getMedicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + itemDto.getMedicineId()));

            PrescriptionItem item = PrescriptionItem.builder()
                    .prescription(prescription)
                    .medicine(med)
                    .dosage(itemDto.getDosage())
                    .frequency(itemDto.getFrequency())
                    .durationDays(itemDto.getDurationDays())
                    .quantityStrips(itemDto.getQuantityStrips())
                    .instructions(itemDto.getInstructions())
                    .build();

            prescription.getItems().add(item);
        }

        return mapToPrescriptionDto(prescriptionRepository.save(prescription));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicDtos.PrescriptionDto> getPrescriptionsByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::mapToPrescriptionDto)
                .collect(Collectors.toList());
    }

    private ClinicDtos.PatientDto mapToPatientDto(Patient p) {
        ClinicDtos.PatientDto dto = new ClinicDtos.PatientDto();
        dto.setId(p.getId());
        dto.setPatientCode(p.getPatientCode());
        dto.setFirstName(p.getFirstName());
        dto.setLastName(p.getLastName());
        dto.setDob(p.getDob());
        dto.setGender(p.getGender());
        dto.setBloodGroup(p.getBloodGroup());
        dto.setPhone(p.getPhone());
        dto.setEmail(p.getEmail());
        dto.setAddress(p.getAddress());
        dto.setEmergencyContactName(p.getEmergencyContactName());
        dto.setEmergencyContactPhone(p.getEmergencyContactPhone());
        dto.setAllergies(p.getAllergies());
        dto.setMedicalHistory(p.getMedicalHistory());
        return dto;
    }

    private ClinicDtos.DepartmentDto mapToDeptDto(Department d) {
        ClinicDtos.DepartmentDto dto = new ClinicDtos.DepartmentDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setCode(d.getCode());
        dto.setDescription(d.getDescription());
        dto.setHeadOfDept(d.getHeadOfDept());
        dto.setIsActive(d.getIsActive());
        return dto;
    }

    private ClinicDtos.DoctorDto mapToDoctorDto(Doctor doc) {
        ClinicDtos.DoctorDto dto = new ClinicDtos.DoctorDto();
        dto.setId(doc.getId());
        dto.setDepartmentId(doc.getDepartment().getId());
        dto.setDepartmentName(doc.getDepartment().getName());
        dto.setDoctorCode(doc.getDoctorCode());
        dto.setFullName(doc.getFullName());
        dto.setSpecialization(doc.getSpecialization());
        dto.setQualification(doc.getQualification());
        dto.setExperienceYears(doc.getExperienceYears());
        dto.setConsultationFee(doc.getConsultationFee());
        dto.setPhone(doc.getPhone());
        dto.setEmail(doc.getEmail());
        dto.setAvailableDays(doc.getAvailableDays());
        dto.setShiftStart(doc.getShiftStart());
        dto.setShiftEnd(doc.getShiftEnd());
        dto.setIsActive(doc.getIsActive());
        return dto;
    }

    private ClinicDtos.AppointmentDto mapToApptDto(Appointment a) {
        ClinicDtos.AppointmentDto dto = new ClinicDtos.AppointmentDto();
        dto.setId(a.getId());
        dto.setAppointmentCode(a.getAppointmentCode());
        dto.setPatientId(a.getPatient().getId());
        dto.setPatientName(a.getPatient().getFullName());
        dto.setPatientPhone(a.getPatient().getPhone());
        dto.setDoctorId(a.getDoctor().getId());
        dto.setDoctorName(a.getDoctor().getFullName());
        dto.setDepartmentName(a.getDoctor().getDepartment().getName());
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setStatus(a.getStatus());
        dto.setReason(a.getReason());
        dto.setConsultationFee(a.getConsultationFee());
        return dto;
    }

    private ClinicDtos.EncounterDto mapToEncounterDto(Encounter e) {
        ClinicDtos.EncounterDto dto = new ClinicDtos.EncounterDto();
        dto.setId(e.getId());
        dto.setEncounterCode(e.getEncounterCode());
        dto.setPatientId(e.getPatient().getId());
        dto.setPatientName(e.getPatient().getFullName());
        dto.setDoctorId(e.getDoctor().getId());
        dto.setDoctorName(e.getDoctor().getFullName());
        if (e.getAppointment() != null) dto.setAppointmentId(e.getAppointment().getId());
        dto.setChiefComplaint(e.getChiefComplaint());
        dto.setVitalsBp(e.getVitalsBp());
        dto.setVitalsPulse(e.getVitalsPulse());
        dto.setVitalsTemp(e.getVitalsTemp());
        dto.setVitalsWeight(e.getVitalsWeight());
        dto.setVitalsSpo2(e.getVitalsSpo2());
        dto.setDiagnosis(e.getDiagnosis());
        dto.setClinicalNotes(e.getClinicalNotes());
        dto.setTreatmentPlan(e.getTreatmentPlan());
        return dto;
    }

    private ClinicDtos.PrescriptionDto mapToPrescriptionDto(Prescription p) {
        ClinicDtos.PrescriptionDto dto = new ClinicDtos.PrescriptionDto();
        dto.setId(p.getId());
        dto.setPrescriptionCode(p.getPrescriptionCode());
        if (p.getEncounter() != null) dto.setEncounterId(p.getEncounter().getId());
        dto.setPatientId(p.getPatient().getId());
        dto.setPatientName(p.getPatient().getFullName());
        dto.setDoctorId(p.getDoctor().getId());
        dto.setDoctorName(p.getDoctor().getFullName());
        dto.setNotes(p.getNotes());
        dto.setItems(p.getItems().stream().map(i -> {
            ClinicDtos.PrescriptionItemDto idto = new ClinicDtos.PrescriptionItemDto();
            idto.setId(i.getId());
            idto.setMedicineId(i.getMedicine().getId());
            idto.setMedicineName(i.getMedicine().getBrandName());
            idto.setStrength(i.getMedicine().getStrength());
            idto.setDosage(i.getDosage());
            idto.setFrequency(i.getFrequency());
            idto.setDurationDays(i.getDurationDays());
            idto.setQuantityStrips(i.getQuantityStrips());
            idto.setInstructions(i.getInstructions());
            return idto;
        }).collect(Collectors.toList()));
        return dto;
    }
}
