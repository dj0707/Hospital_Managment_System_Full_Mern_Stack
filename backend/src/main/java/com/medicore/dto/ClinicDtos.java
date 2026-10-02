package com.medicore.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ClinicDtos {

    @Data
    public static class PatientDto {
        private Long id;
        private String patientCode;
        @NotBlank(message = "First name is required")
        private String firstName;
        @NotBlank(message = "Last name is required")
        private String lastName;
        @NotNull(message = "Date of birth is required")
        private LocalDate dob;
        @NotBlank(message = "Gender is required")
        private String gender;
        private String bloodGroup;
        @NotBlank(message = "Phone number is required")
        private String phone;
        private String email;
        private String address;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String allergies;
        private String medicalHistory;
    }

    @Data
    public static class DepartmentDto {
        private Long id;
        @NotBlank(message = "Department name is required")
        private String name;
        @NotBlank(message = "Department code is required")
        private String code;
        private String description;
        private String headOfDept;
        private Boolean isActive;
    }

    @Data
    public static class DoctorDto {
        private Long id;
        private Long userId;
        @NotNull(message = "Department ID is required")
        private Long departmentId;
        private String departmentName;
        private String doctorCode;
        @NotBlank(message = "Full name is required")
        private String fullName;
        @NotBlank(message = "Specialization is required")
        private String specialization;
        @NotBlank(message = "Qualification is required")
        private String qualification;
        private Integer experienceYears;
        @NotNull(message = "Consultation fee is required")
        private BigDecimal consultationFee;
        @NotBlank(message = "Phone number is required")
        private String phone;
        @NotBlank(message = "Email is required")
        private String email;
        private String availableDays;
        private String shiftStart;
        private String shiftEnd;
        private Boolean isActive;
    }

    @Data
    public static class AppointmentDto {
        private Long id;
        private String appointmentCode;
        @NotNull(message = "Patient ID is required")
        private Long patientId;
        private String patientName;
        private String patientPhone;
        @NotNull(message = "Doctor ID is required")
        private Long doctorId;
        private String doctorName;
        private String departmentName;
        @NotNull(message = "Appointment date is required")
        private LocalDate appointmentDate;
        @NotNull(message = "Appointment time is required")
        private LocalTime appointmentTime;
        private String status;
        private String reason;
        private BigDecimal consultationFee;
    }

    @Data
    public static class EncounterDto {
        private Long id;
        private String encounterCode;
        @NotNull(message = "Patient ID is required")
        private Long patientId;
        private String patientName;
        @NotNull(message = "Doctor ID is required")
        private Long doctorId;
        private String doctorName;
        private Long appointmentId;
        private String chiefComplaint;
        private String vitalsBp;
        private String vitalsPulse;
        private String vitalsTemp;
        private String vitalsWeight;
        private String vitalsSpo2;
        private String diagnosis;
        private String clinicalNotes;
        private String treatmentPlan;
    }

    @Data
    public static class PrescriptionItemDto {
        private Long id;
        @NotNull(message = "Medicine ID is required")
        private Long medicineId;
        private String medicineName;
        private String strength;
        @NotBlank(message = "Dosage instruction is required (e.g., 1-0-1)")
        private String dosage;
        @NotBlank(message = "Frequency is required (e.g., After Food)")
        private String frequency;
        @NotNull(message = "Duration in days is required")
        private Integer durationDays;
        @NotNull(message = "Quantity strips is required")
        private Integer quantityStrips;
        private String instructions;
    }

    @Data
    public static class PrescriptionDto {
        private Long id;
        private String prescriptionCode;
        private Long encounterId;
        @NotNull(message = "Patient ID is required")
        private Long patientId;
        private String patientName;
        @NotNull(message = "Doctor ID is required")
        private Long doctorId;
        private String doctorName;
        private String notes;
        @NotEmpty(message = "Prescription must contain at least one item")
        private List<PrescriptionItemDto> items;
    }
}
