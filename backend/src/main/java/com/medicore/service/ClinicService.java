package com.medicore.service;

import com.medicore.dto.ClinicDtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ClinicService {
    // Patients
    ClinicDtos.PatientDto createPatient(ClinicDtos.PatientDto dto);
    ClinicDtos.PatientDto updatePatient(Long id, ClinicDtos.PatientDto dto);
    ClinicDtos.PatientDto getPatientById(Long id);
    ClinicDtos.PatientDto getPatientByCode(String code);
    Page<ClinicDtos.PatientDto> searchPatients(String query, Pageable pageable);

    // Departments
    ClinicDtos.DepartmentDto createDepartment(ClinicDtos.DepartmentDto dto);
    List<ClinicDtos.DepartmentDto> getActiveDepartments();

    // Doctors
    ClinicDtos.DoctorDto createDoctor(ClinicDtos.DoctorDto dto);
    ClinicDtos.DoctorDto updateDoctor(Long id, ClinicDtos.DoctorDto dto);
    ClinicDtos.DoctorDto getDoctorById(Long id);
    List<ClinicDtos.DoctorDto> getDoctorsByDepartment(Long departmentId);
    Page<ClinicDtos.DoctorDto> searchDoctors(String query, Pageable pageable);

    // Appointments
    ClinicDtos.AppointmentDto bookAppointment(ClinicDtos.AppointmentDto dto);
    ClinicDtos.AppointmentDto updateAppointmentStatus(Long id, String status);
    Page<ClinicDtos.AppointmentDto> filterAppointments(Long doctorId, Long patientId, String status, LocalDate date, Pageable pageable);

    // Encounters & Medical Records
    ClinicDtos.EncounterDto createEncounter(ClinicDtos.EncounterDto dto);
    List<ClinicDtos.EncounterDto> getEncountersByPatient(Long patientId);

    // Prescriptions
    ClinicDtos.PrescriptionDto createPrescription(ClinicDtos.PrescriptionDto dto);
    List<ClinicDtos.PrescriptionDto> getPrescriptionsByPatient(Long patientId);
}
