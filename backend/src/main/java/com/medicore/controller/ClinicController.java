package com.medicore.controller;

import com.medicore.dto.ClinicDtos;
import com.medicore.service.ClinicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clinic")
@RequiredArgsConstructor
@Tag(name = "Clinic & Clinical Operations", description = "Patients, Doctors, Appointments, Encounters, Prescriptions")
public class ClinicController {

    private final ClinicService clinicService;

    // PATIENTS — Strictly restricted to ADMIN for full management, with search allowed for Pharmacy Billing
    @PostMapping("/patients")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new patient into EHR (Admin only)")
    public ResponseEntity<ClinicDtos.PatientDto> createPatient(@Valid @RequestBody ClinicDtos.PatientDto dto) {
        return ResponseEntity.ok(clinicService.createPatient(dto));
    }

    @PutMapping("/patients/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update patient demographics (Admin only)")
    public ResponseEntity<ClinicDtos.PatientDto> updatePatient(@PathVariable Long id, @Valid @RequestBody ClinicDtos.PatientDto dto) {
        return ResponseEntity.ok(clinicService.updatePatient(id, dto));
    }

    @GetMapping("/patients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST', 'PATIENT')")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<ClinicDtos.PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.getPatientById(id));
    }

    @GetMapping("/patients/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'DOCTOR', 'RECEPTIONIST')")
    @Operation(summary = "Search patients for EHR and Medicine Billing")
    public ResponseEntity<Page<ClinicDtos.PatientDto>> searchPatients(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clinicService.searchPatients(query, PageRequest.of(page, size)));
    }

    // DEPARTMENTS
    @PostMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create medical department (Admin only)")
    public ResponseEntity<ClinicDtos.DepartmentDto> createDepartment(@Valid @RequestBody ClinicDtos.DepartmentDto dto) {
        return ResponseEntity.ok(clinicService.createDepartment(dto));
    }

    @GetMapping("/departments")
    @Operation(summary = "List all active departments")
    public ResponseEntity<List<ClinicDtos.DepartmentDto>> getDepartments() {
        return ResponseEntity.ok(clinicService.getActiveDepartments());
    }

    // DOCTORS
    @PostMapping("/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new doctor (Admin only)")
    public ResponseEntity<ClinicDtos.DoctorDto> createDoctor(@Valid @RequestBody ClinicDtos.DoctorDto dto) {
        return ResponseEntity.ok(clinicService.createDoctor(dto));
    }

    @PutMapping("/doctors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update doctor details (Admin only)")
    public ResponseEntity<ClinicDtos.DoctorDto> updateDoctor(@PathVariable Long id, @Valid @RequestBody ClinicDtos.DoctorDto dto) {
        return ResponseEntity.ok(clinicService.updateDoctor(id, dto));
    }

    @GetMapping("/doctors/{id}")
    @Operation(summary = "Get doctor details by ID")
    public ResponseEntity<ClinicDtos.DoctorDto> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(clinicService.getDoctorById(id));
    }

    @GetMapping("/doctors/search")
    @Operation(summary = "Search active doctors")
    public ResponseEntity<Page<ClinicDtos.DoctorDto>> searchDoctors(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clinicService.searchDoctors(query, PageRequest.of(page, size)));
    }

    // APPOINTMENTS
    @PostMapping("/appointments")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT')")
    @Operation(summary = "Book an appointment")
    public ResponseEntity<ClinicDtos.AppointmentDto> bookAppointment(@Valid @RequestBody ClinicDtos.AppointmentDto dto) {
        return ResponseEntity.ok(clinicService.bookAppointment(dto));
    }

    @PatchMapping("/appointments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    @Operation(summary = "Update appointment status")
    public ResponseEntity<ClinicDtos.AppointmentDto> updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(clinicService.updateAppointmentStatus(id, status));
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PATIENT')")
    @Operation(summary = "Filter appointments")
    public ResponseEntity<Page<ClinicDtos.AppointmentDto>> filterAppointments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clinicService.filterAppointments(doctorId, patientId, status, date, PageRequest.of(page, size)));
    }

    // ENCOUNTERS & MEDICAL RECORDS
    @PostMapping("/encounters")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Record clinical encounter and vitals (Doctor / Admin)")
    public ResponseEntity<ClinicDtos.EncounterDto> createEncounter(@Valid @RequestBody ClinicDtos.EncounterDto dto) {
        return ResponseEntity.ok(clinicService.createEncounter(dto));
    }

    @GetMapping("/encounters/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Get clinical history of patient")
    public ResponseEntity<List<ClinicDtos.EncounterDto>> getPatientEncounters(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicService.getEncountersByPatient(patientId));
    }

    // PRESCRIPTIONS
    @PostMapping("/prescriptions")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Issue medical prescription (Doctor / Admin)")
    public ResponseEntity<ClinicDtos.PrescriptionDto> createPrescription(@Valid @RequestBody ClinicDtos.PrescriptionDto dto) {
        return ResponseEntity.ok(clinicService.createPrescription(dto));
    }

    @GetMapping("/prescriptions/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST', 'PATIENT')")
    @Operation(summary = "Get prescriptions for a patient")
    public ResponseEntity<List<ClinicDtos.PrescriptionDto>> getPatientPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicService.getPrescriptionsByPatient(patientId));
    }
}
