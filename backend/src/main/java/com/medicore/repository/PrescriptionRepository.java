package com.medicore.repository;

import com.medicore.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByPrescriptionCode(String prescriptionCode);
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    Page<Prescription> findByPatientId(Long patientId, Pageable pageable);
}
