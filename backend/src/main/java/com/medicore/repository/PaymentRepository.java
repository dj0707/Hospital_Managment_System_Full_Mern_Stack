package com.medicore.repository;

import com.medicore.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceId(Long invoiceId);
    
    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate GROUP BY p.paymentMethod")
    List<Object[]> getPaymentBreakdownByMethod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
