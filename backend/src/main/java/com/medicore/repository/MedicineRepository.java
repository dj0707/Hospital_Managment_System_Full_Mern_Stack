package com.medicore.repository;

import com.medicore.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findBySku(String sku);
    boolean existsBySku(String sku);

    @Query("SELECT m FROM Medicine m WHERE m.isActive = true AND " +
           "(LOWER(m.brandName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Medicine> searchMedicines(@Param("query") String query, Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE m.isActive = true AND " +
           "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
           "(:query IS NULL OR (LOWER(m.brandName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.sku) LIKE LOWER(CONCAT('%', :query, '%'))))")
    Page<Medicine> filterMedicines(@Param("categoryId") Long categoryId, @Param("query") String query, Pageable pageable);

    long countByIsActiveTrue();
}
