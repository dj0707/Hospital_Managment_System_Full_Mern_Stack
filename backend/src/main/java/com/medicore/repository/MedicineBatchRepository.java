package com.medicore.repository;

import com.medicore.entity.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Long> {
    List<MedicineBatch> findByMedicineId(Long medicineId);
    
    // FEFO: First-Expiry-First-Out for non-expired batches with positive stock
    @Query("SELECT b FROM MedicineBatch b WHERE b.medicine.id = :medicineId AND b.expiryDate > :currentDate AND b.quantityInBaseUnits > 0 ORDER BY b.expiryDate ASC")
    List<MedicineBatch> findActiveBatchesFEFO(@Param("medicineId") Long medicineId, @Param("currentDate") LocalDate currentDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM MedicineBatch b WHERE b.medicine.id = :medicineId AND b.expiryDate > :currentDate AND b.quantityInBaseUnits > 0 ORDER BY b.expiryDate ASC")
    List<MedicineBatch> findActiveBatchesFEFOWithLock(@Param("medicineId") Long medicineId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COALESCE(SUM(b.quantityInBaseUnits), 0) FROM MedicineBatch b WHERE b.medicine.id = :medicineId AND b.expiryDate > :currentDate")
    int getTotalAvailableStockInBaseUnits(@Param("medicineId") Long medicineId, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT b FROM MedicineBatch b WHERE b.expiryDate <= :targetDate AND b.quantityInBaseUnits > 0 ORDER BY b.expiryDate ASC")
    List<MedicineBatch> findNearExpiryBatches(@Param("targetDate") LocalDate targetDate);
    
    @Query("SELECT b FROM MedicineBatch b WHERE b.expiryDate <= :currentDate AND b.quantityInBaseUnits > 0")
    List<MedicineBatch> findExpiredBatchesWithStock(@Param("currentDate") LocalDate currentDate);
}
