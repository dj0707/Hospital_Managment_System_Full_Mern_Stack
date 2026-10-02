package com.medicore.repository;

import com.medicore.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByDoctorCode(String doctorCode);
    Optional<Doctor> findByUserId(Long userId);
    List<Doctor> findByDepartmentIdAndIsActiveTrue(Long departmentId);
    List<Doctor> findByIsActiveTrue();
    
    @Query("SELECT d FROM Doctor d WHERE d.isActive = true AND " +
           "(LOWER(d.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.doctorCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Doctor> searchDoctors(@Param("query") String query, Pageable pageable);

    long countByIsActiveTrue();
}
