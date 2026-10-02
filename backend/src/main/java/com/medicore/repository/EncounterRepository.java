package com.medicore.repository;

import com.medicore.entity.Encounter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncounterRepository extends JpaRepository<Encounter, Long> {
    Optional<Encounter> findByEncounterCode(String encounterCode);
    List<Encounter> findByPatientIdOrderByEncounterDateDesc(Long patientId);
    Page<Encounter> findByPatientId(Long patientId, Pageable pageable);
}
