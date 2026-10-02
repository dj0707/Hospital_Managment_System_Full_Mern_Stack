package com.medicore.service;

import com.medicore.dto.HospitalDtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HospitalService {
    // Wards & Beds
    HospitalDtos.WardDto createWard(HospitalDtos.WardDto dto);
    List<HospitalDtos.WardDto> getAllWards();
    HospitalDtos.BedDto createBed(HospitalDtos.BedDto dto);
    List<HospitalDtos.BedDto> getBedsByWard(Long wardId);
    List<HospitalDtos.BedDto> getAvailableBeds();

    // Admissions
    HospitalDtos.AdmissionDto admitPatient(HospitalDtos.AdmissionDto dto);
    HospitalDtos.AdmissionDto dischargePatient(Long admissionId);
    List<HospitalDtos.AdmissionDto> getActiveAdmissions();

    // Dashboard & Analytics
    HospitalDtos.DashboardStatsDto getDashboardStats();

    // AI Assistant
    HospitalDtos.AiAssistantResponse askAiAssistant(HospitalDtos.AiAssistantRequest request);
}
