package com.medicore.controller;

import com.medicore.dto.PharmacyDtos;
import com.medicore.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacy")
@RequiredArgsConstructor
@Tag(name = "Pharmacy & Inventory POS", description = "Medicine Catalog, FEFO Inventory, Batches, and Pharmacy POS Billing")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    // CATEGORIES
    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Create medicine category")
    public ResponseEntity<PharmacyDtos.CategoryDto> createCategory(@Valid @RequestBody PharmacyDtos.CategoryDto dto) {
        return ResponseEntity.ok(pharmacyService.createCategory(dto));
    }

    @GetMapping("/categories")
    @Operation(summary = "List all medicine categories")
    public ResponseEntity<List<PharmacyDtos.CategoryDto>> getCategories() {
        return ResponseEntity.ok(pharmacyService.getAllCategories());
    }

    // SUPPLIERS
    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Create supplier profile")
    public ResponseEntity<PharmacyDtos.SupplierDto> createSupplier(@Valid @RequestBody PharmacyDtos.SupplierDto dto) {
        return ResponseEntity.ok(pharmacyService.createSupplier(dto));
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "List all active suppliers")
    public ResponseEntity<List<PharmacyDtos.SupplierDto>> getSuppliers() {
        return ResponseEntity.ok(pharmacyService.getAllActiveSuppliers());
    }

    // MEDICINES CATALOG
    @PostMapping("/medicines")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Add new medicine into catalog")
    public ResponseEntity<PharmacyDtos.MedicineDto> createMedicine(@Valid @RequestBody PharmacyDtos.MedicineDto dto) {
        return ResponseEntity.ok(pharmacyService.createMedicine(dto));
    }

    @PutMapping("/medicines/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Update medicine details")
    public ResponseEntity<PharmacyDtos.MedicineDto> updateMedicine(@PathVariable Long id, @Valid @RequestBody PharmacyDtos.MedicineDto dto) {
        return ResponseEntity.ok(pharmacyService.updateMedicine(id, dto));
    }

    @GetMapping("/medicines/{id}")
    @Operation(summary = "Get medicine by ID")
    public ResponseEntity<PharmacyDtos.MedicineDto> getMedicineById(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getMedicineById(id));
    }

    @GetMapping("/medicines/search")
    @Operation(summary = "Search medicine catalog by brand, generic, or SKU")
    public ResponseEntity<Page<PharmacyDtos.MedicineDto>> searchMedicines(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pharmacyService.searchMedicines(query, PageRequest.of(page, size)));
    }

    @GetMapping("/medicines")
    @Operation(summary = "Filter medicines by category or keyword")
    public ResponseEntity<Page<PharmacyDtos.MedicineDto>> filterMedicines(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pharmacyService.filterMedicines(categoryId, query, PageRequest.of(page, size)));
    }

    // BATCHES & INVENTORY
    @PostMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Receive and register a new medicine batch")
    public ResponseEntity<PharmacyDtos.MedicineBatchDto> addBatch(@Valid @RequestBody PharmacyDtos.MedicineBatchDto dto) {
        return ResponseEntity.ok(pharmacyService.addBatch(dto));
    }

    @GetMapping("/batches/medicine/{medicineId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "List batches for a specific medicine")
    public ResponseEntity<List<PharmacyDtos.MedicineBatchDto>> getBatchesByMedicine(@PathVariable Long medicineId) {
        return ResponseEntity.ok(pharmacyService.getBatchesByMedicine(medicineId));
    }

    @GetMapping("/batches/near-expiry")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get batches expiring within specified days (default: 30)")
    public ResponseEntity<List<PharmacyDtos.MedicineBatchDto>> getNearExpiryBatches(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(pharmacyService.getNearExpiryBatches(days));
    }

    @PostMapping("/stock/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    @Operation(summary = "Record stock adjustment or expired disposal")
    public ResponseEntity<String> adjustStock(@Valid @RequestBody PharmacyDtos.StockAdjustmentDto dto) {
        pharmacyService.adjustStock(dto);
        return ResponseEntity.ok("Stock adjustment successfully applied and audited.");
    }

    // POS CHECKOUT & BILLING (TOP PRIORITY)
    @PostMapping("/pos/checkout")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'RECEPTIONIST')")
    @Operation(summary = "Execute atomic pharmacy POS sale with FEFO batch allocation")
    public ResponseEntity<PharmacyDtos.InvoiceResponse> checkout(@Valid @RequestBody PharmacyDtos.CheckoutRequest request) {
        return ResponseEntity.ok(pharmacyService.checkout(request));
    }

    @GetMapping("/invoices/{invoiceNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'RECEPTIONIST', 'ACCOUNTANT', 'PATIENT')")
    @Operation(summary = "Retrieve invoice details by invoice number")
    public ResponseEntity<PharmacyDtos.InvoiceResponse> getInvoice(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(pharmacyService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST', 'RECEPTIONIST', 'ACCOUNTANT')")
    @Operation(summary = "Filter and search invoices")
    public ResponseEntity<Page<PharmacyDtos.InvoiceResponse>> filterInvoices(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pharmacyService.filterInvoices(query, type, status, startDate, endDate, PageRequest.of(page, size)));
    }
}
