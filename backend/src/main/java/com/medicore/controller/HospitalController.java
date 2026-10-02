package com.medicore.controller;

import com.medicore.dto.HospitalDtos;
import com.medicore.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hospital")
@RequiredArgsConstructor
@Tag(name = "Hospital Administration & AI", description = "Wards, Beds, Inpatient Admissions, Analytics, and AI Operations Assistant")
public class HospitalController {

    private final HospitalService hospitalService;

    // WARDS
    @PostMapping("/wards")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a hospital ward")
    public ResponseEntity<HospitalDtos.WardDto> createWard(@Valid @RequestBody HospitalDtos.WardDto dto) {
        return ResponseEntity.ok(hospitalService.createWard(dto));
    }

    @GetMapping("/wards")
    @Operation(summary = "List all hospital wards")
    public ResponseEntity<List<HospitalDtos.WardDto>> getAllWards() {
        return ResponseEntity.ok(hospitalService.getAllWards());
    }

    // BEDS
    @PostMapping("/beds")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a bed in a ward")
    public ResponseEntity<HospitalDtos.BedDto> createBed(@Valid @RequestBody HospitalDtos.BedDto dto) {
        return ResponseEntity.ok(hospitalService.createBed(dto));
    }

    @GetMapping("/beds/ward/{wardId}")
    @Operation(summary = "Get beds in a specific ward")
    public ResponseEntity<List<HospitalDtos.BedDto>> getBedsByWard(@PathVariable Long wardId) {
        return ResponseEntity.ok(hospitalService.getBedsByWard(wardId));
    }

    @GetMapping("/beds/available")
    @Operation(summary = "Get currently available beds")
    public ResponseEntity<List<HospitalDtos.BedDto>> getAvailableBeds() {
        return ResponseEntity.ok(hospitalService.getAvailableBeds());
    }

    // ADMISSIONS
    @PostMapping("/admissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    @Operation(summary = "Admit a patient to a bed")
    public ResponseEntity<HospitalDtos.AdmissionDto> admitPatient(@Valid @RequestBody HospitalDtos.AdmissionDto dto) {
        return ResponseEntity.ok(hospitalService.admitPatient(dto));
    }

    @PostMapping("/admissions/{id}/discharge")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    @Operation(summary = "Discharge patient and calculate stay billing")
    public ResponseEntity<HospitalDtos.AdmissionDto> dischargePatient(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.dischargePatient(id));
    }

    @GetMapping("/admissions/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR')")
    @Operation(summary = "Get list of active inpatient admissions")
    public ResponseEntity<List<HospitalDtos.AdmissionDto>> getActiveAdmissions() {
        return ResponseEntity.ok(hospitalService.getActiveAdmissions());
    }

    // DASHBOARD METRICS
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'DOCTOR', 'PHARMACIST', 'ACCOUNTANT')")
    @Operation(summary = "Get live operational dashboard metrics")
    public ResponseEntity<HospitalDtos.DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(hospitalService.getDashboardStats());
    }

    // AI ASSISTANT
    @PostMapping("/ai/ask")
    @Operation(summary = "Ask the AI Hospital Operations Assistant")
    public ResponseEntity<HospitalDtos.AiAssistantResponse> askAi(@Valid @RequestBody HospitalDtos.AiAssistantRequest request) {
        return ResponseEntity.ok(hospitalService.askAiAssistant(request));
    }
}
