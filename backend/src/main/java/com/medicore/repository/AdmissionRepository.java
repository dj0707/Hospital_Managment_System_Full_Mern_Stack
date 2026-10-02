package com.medicore.repository;

import com.medicore.entity.Admission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    Optional<Admission> findByAdmissionCode(String admissionCode);
    List<Admission> findByPatientIdOrderByAdmissionDateDesc(Long patientId);
    
    @Query("SELECT COUNT(a) > 0 FROM Admission a WHERE a.bed.id = :bedId AND a.status = 'ADMITTED'")
    boolean existsActiveAdmissionForBed(@Param("bedId") Long bedId);

    @Query("SELECT a FROM Admission a WHERE a.status = 'ADMITTED'")
    List<Admission> findCurrentActiveAdmissions();

    long countByStatus(String status);
}
