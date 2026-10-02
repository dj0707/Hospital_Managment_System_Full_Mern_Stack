package com.medicore.service;

import com.medicore.dto.PharmacyDtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PharmacyService {
    // Categories
    PharmacyDtos.CategoryDto createCategory(PharmacyDtos.CategoryDto dto);
    List<PharmacyDtos.CategoryDto> getAllCategories();

    // Suppliers
    PharmacyDtos.SupplierDto createSupplier(PharmacyDtos.SupplierDto dto);
    List<PharmacyDtos.SupplierDto> getAllActiveSuppliers();

    // Medicines
    PharmacyDtos.MedicineDto createMedicine(PharmacyDtos.MedicineDto dto);
    PharmacyDtos.MedicineDto updateMedicine(Long id, PharmacyDtos.MedicineDto dto);
    PharmacyDtos.MedicineDto getMedicineById(Long id);
    Page<PharmacyDtos.MedicineDto> searchMedicines(String query, Pageable pageable);
    Page<PharmacyDtos.MedicineDto> filterMedicines(Long categoryId, String query, Pageable pageable);

    // Batches & Stock
    PharmacyDtos.MedicineBatchDto addBatch(PharmacyDtos.MedicineBatchDto dto);
    List<PharmacyDtos.MedicineBatchDto> getBatchesByMedicine(Long medicineId);
    List<PharmacyDtos.MedicineBatchDto> getNearExpiryBatches(int days);
    void adjustStock(PharmacyDtos.StockAdjustmentDto dto);

    // POS Checkout & Invoicing
    PharmacyDtos.InvoiceResponse checkout(PharmacyDtos.CheckoutRequest request);
    PharmacyDtos.InvoiceResponse getInvoiceByNumber(String invoiceNumber);
    Page<PharmacyDtos.InvoiceResponse> filterInvoices(String query, String type, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
