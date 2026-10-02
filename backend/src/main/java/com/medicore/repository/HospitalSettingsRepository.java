package com.medicore.repository;

import com.medicore.entity.HospitalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalSettingsRepository extends JpaRepository<HospitalSettings, Long> {
    Optional<HospitalSettings> findFirstByOrderByIdAsc();
}
