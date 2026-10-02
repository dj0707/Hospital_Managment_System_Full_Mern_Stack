package com.medicore.repository;

import com.medicore.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    boolean existsByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:query IS NULL OR (LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.customerPhone) LIKE LOWER(CONCAT('%', :query, '%')))) AND " +
           "(:type IS NULL OR i.invoiceType = :type) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:startDate IS NULL OR i.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR i.createdAt <= :endDate)")
    Page<Invoice> filterInvoices(
            @Param("query") String query,
            @Param("type") String type,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.status = 'COMPLETED' AND i.createdAt >= :startDate AND i.createdAt <= :endDate")
    BigDecimal sumTotalSalesBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i WHERE i.status = 'COMPLETED' AND i.createdAt >= :startDate AND i.createdAt <= :endDate")
    BigDecimal sumCollectedAmountBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(i.balanceAmount), 0) FROM Invoice i WHERE i.status = 'COMPLETED' AND i.paymentStatus <> 'PAID'")
    BigDecimal sumOutstandingBalance();

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
