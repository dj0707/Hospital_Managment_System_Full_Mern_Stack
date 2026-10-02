package com.medicore.repository;

import com.medicore.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    Optional<Bed> findByBedNumber(String bedNumber);
    List<Bed> findByWardId(Long wardId);
    List<Bed> findByStatus(String status);
    long countByStatus(String status);
}
