package com.medicore.repository;

import com.medicore.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByMedicineIdOrderByCreatedAtDesc(Long medicineId);
    
    @Query("SELECT sm FROM StockMovement sm WHERE " +
           "(:medicineId IS NULL OR sm.medicine.id = :medicineId) AND " +
           "(:movementType IS NULL OR sm.movementType = :movementType)")
    Page<StockMovement> filterMovements(@Param("medicineId") Long medicineId, @Param("movementType") String movementType, Pageable pageable);
}
