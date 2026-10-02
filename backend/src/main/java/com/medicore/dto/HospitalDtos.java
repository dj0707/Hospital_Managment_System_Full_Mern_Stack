package com.medicore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HospitalDtos {

    @Data
    public static class WardDto {
        private Long id;
        @NotBlank(message = "Ward name is required")
        private String name;
        @NotBlank(message = "Ward type is required")
        private String wardType;
        @NotNull(message = "Daily rate is required")
        private BigDecimal dailyRate;
        private Boolean isActive;
    }

    @Data
    public static class BedDto {
        private Long id;
        @NotBlank(message = "Bed number is required")
        private String bedNumber;
        @NotNull(message = "Ward ID is required")
        private Long wardId;
        private String wardName;
        private String wardType;
        private BigDecimal dailyRate;
        private String status;
    }

    @Data
    public static class AdmissionDto {
        private Long id;
        private String admissionCode;
        @NotNull(message = "Patient ID is required")
        private Long patientId;
        private String patientName;
        @NotNull(message = "Bed ID is required")
        private Long bedId;
        private String bedNumber;
        private String wardName;
        @NotNull(message = "Doctor ID is required")
        private Long doctorId;
        private String doctorName;
        private LocalDateTime admissionDate;
        private LocalDateTime dischargeDate;
        private String diagnosis;
        private String status;
        private BigDecimal totalBedCharge;
    }

    @Data
    public static class DashboardStatsDto {
        private long totalPatients;
        private long activeDoctors;
        private long todayAppointments;
        private long activeAdmissions;
        private long availableBeds;
        private long totalMedicines;
        private long lowStockMedicines;
        private BigDecimal todaySales;
        private BigDecimal monthlySales;
        private BigDecimal outstandingBalance;
    }

    @Data
    public static class AiAssistantRequest {
        @NotBlank(message = "Prompt is required")
        private String prompt;
        private String moduleContext;
    }

    @Data
    public static class AiAssistantResponse {
        private String answer;
        private boolean isConfigured;
        private String disclaimer;
    }
}
