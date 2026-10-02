package com.medicore.service.impl;

import com.medicore.dto.PharmacyDtos;
import com.medicore.entity.*;
import com.medicore.exception.BadRequestException;
import com.medicore.exception.ConflictException;
import com.medicore.exception.ResourceNotFoundException;
import com.medicore.repository.*;
import com.medicore.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {

    private final MedicineCategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineBatchRepository batchRepository;
    private final StockMovementRepository stockMovementRepository;
    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return userRepository.findByUsername("admin").orElse(null);
        }
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    @Override
    @Transactional
    public PharmacyDtos.CategoryDto createCategory(PharmacyDtos.CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new ConflictException("Category with name " + dto.getName() + " already exists");
        }
        MedicineCategory cat = MedicineCategory.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        MedicineCategory saved = categoryRepository.save(cat);
        PharmacyDtos.CategoryDto res = new PharmacyDtos.CategoryDto();
        res.setId(saved.getId());
        res.setName(saved.getName());
        res.setDescription(saved.getDescription());
        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyDtos.CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream().map(c -> {
            PharmacyDtos.CategoryDto dto = new PharmacyDtos.CategoryDto();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setDescription(c.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PharmacyDtos.SupplierDto createSupplier(PharmacyDtos.SupplierDto dto) {
        String code = "SUP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Supplier s = Supplier.builder()
                .supplierCode(code)
                .name(dto.getName())
                .contactPerson(dto.getContactPerson())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .gstNumber(dto.getGstNumber())
                .isActive(true)
                .build();
        Supplier saved = supplierRepository.save(s);
        return mapToSupplierDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyDtos.SupplierDto> getAllActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue().stream()
                .map(this::mapToSupplierDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PharmacyDtos.MedicineDto createMedicine(PharmacyDtos.MedicineDto dto) {
        if (medicineRepository.existsBySku(dto.getSku())) {
            throw new ConflictException("Medicine with SKU " + dto.getSku() + " already exists");
        }
        MedicineCategory cat = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));

        Medicine med = Medicine.builder()
                .sku(dto.getSku())
                .brandName(dto.getBrandName())
                .genericName(dto.getGenericName())
                .category(cat)
                .dosageForm(dto.getDosageForm())
                .strength(dto.getStrength())
                .manufacturer(dto.getManufacturer())
                .unitsPerStrip(dto.getUnitsPerStrip() != null ? dto.getUnitsPerStrip() : 10)
                .baseUnit(dto.getBaseUnit() != null ? dto.getBaseUnit() : "Tablet")
                .costPerStrip(dto.getCostPerStrip())
                .pricePerStrip(dto.getPricePerStrip())
                .taxPercent(dto.getTaxPercent() != null ? dto.getTaxPercent() : BigDecimal.ZERO)
                .reorderLevelStrips(dto.getReorderLevelStrips() != null ? dto.getReorderLevelStrips() : 10)
                .prescriptionRequired(dto.getPrescriptionRequired() != null ? dto.getPrescriptionRequired() : false)
                .isActive(true)
                .build();

        Medicine saved = medicineRepository.save(med);
        return mapToMedicineDto(saved);
    }

    @Override
    @Transactional
    public PharmacyDtos.MedicineDto updateMedicine(Long id, PharmacyDtos.MedicineDto dto) {
        Medicine med = medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));

        if (dto.getCategoryId() != null) {
            MedicineCategory cat = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            med.setCategory(cat);
        }

        med.setBrandName(dto.getBrandName());
        med.setGenericName(dto.getGenericName());
        med.setDosageForm(dto.getDosageForm());
        med.setStrength(dto.getStrength());
        med.setManufacturer(dto.getManufacturer());
        if (dto.getUnitsPerStrip() != null) med.setUnitsPerStrip(dto.getUnitsPerStrip());
        if (dto.getBaseUnit() != null) med.setBaseUnit(dto.getBaseUnit());
        med.setCostPerStrip(dto.getCostPerStrip());
        med.setPricePerStrip(dto.getPricePerStrip());
        if (dto.getTaxPercent() != null) med.setTaxPercent(dto.getTaxPercent());
        if (dto.getReorderLevelStrips() != null) med.setReorderLevelStrips(dto.getReorderLevelStrips());
        if (dto.getPrescriptionRequired() != null) med.setPrescriptionRequired(dto.getPrescriptionRequired());
        if (dto.getIsActive() != null) med.setIsActive(dto.getIsActive());

        return mapToMedicineDto(medicineRepository.save(med));
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacyDtos.MedicineDto getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .map(this::mapToMedicineDto)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PharmacyDtos.MedicineDto> searchMedicines(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return medicineRepository.findAll(pageable).map(this::mapToMedicineDto);
        }
        return medicineRepository.searchMedicines(query, pageable).map(this::mapToMedicineDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PharmacyDtos.MedicineDto> filterMedicines(Long categoryId, String query, Pageable pageable) {
        return medicineRepository.filterMedicines(categoryId, query, pageable).map(this::mapToMedicineDto);
    }

    @Override
    @Transactional
    public PharmacyDtos.MedicineBatchDto addBatch(PharmacyDtos.MedicineBatchDto dto) {
        Medicine med = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + dto.getMedicineId()));

        Supplier supplier = null;
        if (dto.getSupplierId() != null) {
            supplier = supplierRepository.findById(dto.getSupplierId()).orElse(null);
        }

        // Calculate quantity in base units: strips * unitsPerStrip or direct base units
        int baseUnits = dto.getQuantityInBaseUnits() != null ? dto.getQuantityInBaseUnits() : 
                (dto.getQuantityInStrips() != null ? dto.getQuantityInStrips() * med.getUnitsPerStrip() : 0);

        MedicineBatch batch = MedicineBatch.builder()
                .medicine(med)
                .supplier(supplier)
                .batchNumber(dto.getBatchNumber())
                .expiryDate(dto.getExpiryDate())
                .manufacturingDate(dto.getManufacturingDate())
                .quantityInBaseUnits(baseUnits)
                .purchaseCostPerStrip(dto.getPurchaseCostPerStrip() != null ? dto.getPurchaseCostPerStrip() : med.getCostPerStrip())
                .mrpPerStrip(dto.getMrpPerStrip() != null ? dto.getMrpPerStrip() : med.getPricePerStrip())
                .build();

        MedicineBatch saved = batchRepository.save(batch);

        // Record stock movement
        User user = getCurrentUser();
        stockMovementRepository.save(StockMovement.builder()
                .medicine(med)
                .batch(saved)
                .movementType("PURCHASE")
                .quantityBaseUnits(baseUnits)
                .referenceId("BATCH-" + saved.getBatchNumber())
                .reason("New batch stock inward")
                .performedBy(user)
                .build());

        return mapToBatchDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyDtos.MedicineBatchDto> getBatchesByMedicine(Long medicineId) {
        return batchRepository.findByMedicineId(medicineId).stream()
                .map(this::mapToBatchDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PharmacyDtos.MedicineBatchDto> getNearExpiryBatches(int days) {
        LocalDate target = LocalDate.now().plusDays(days);
        return batchRepository.findNearExpiryBatches(target).stream()
                .map(this::mapToBatchDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void adjustStock(PharmacyDtos.StockAdjustmentDto dto) {
        Medicine med = medicineRepository.findById(dto.getMedicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + dto.getMedicineId()));

        MedicineBatch batch = batchRepository.findById(dto.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + dto.getBatchId()));

        int qty = dto.getQuantityBaseUnits();
        if (qty <= 0) {
            throw new BadRequestException("Adjustment quantity must be greater than zero");
        }

        if ("ADJUSTMENT_ADD".equalsIgnoreCase(dto.getMovementType())) {
            batch.setQuantityInBaseUnits(batch.getQuantityInBaseUnits() + qty);
        } else if ("ADJUSTMENT_DEDUCT".equalsIgnoreCase(dto.getMovementType()) || "EXPIRED_DISPOSAL".equalsIgnoreCase(dto.getMovementType())) {
            if (batch.getQuantityInBaseUnits() < qty) {
                throw new BadRequestException("Cannot deduct " + qty + " base units; available in batch: " + batch.getQuantityInBaseUnits());
            }
            batch.setQuantityInBaseUnits(batch.getQuantityInBaseUnits() - qty);
        } else {
            throw new BadRequestException("Invalid stock movement type: " + dto.getMovementType());
        }

        batchRepository.save(batch);

        User user = getCurrentUser();
        stockMovementRepository.save(StockMovement.builder()
                .medicine(med)
                .batch(batch)
                .movementType(dto.getMovementType().toUpperCase())
                .quantityBaseUnits(qty)
                .reason(dto.getReason())
                .performedBy(user)
                .build());

        auditLogRepository.save(AuditLog.builder()
                .userId(user != null ? user.getId() : null)
                .username(user != null ? user.getUsername() : "SYSTEM")
                .action("ADJUST_STOCK")
                .resourceType("MEDICINE_BATCH")
                .resourceId(batch.getId().toString())
                .details("Adjusted batch " + batch.getBatchNumber() + " by " + qty + " base units with reason: " + dto.getReason())
                .build());
    }

    /**
     * TOP PRIORITY: Robust Atomic Pharmacy POS Checkout with FEFO batch allocation, strip conversion, and concurrency protection.
     */
    @Override
    @Transactional
    public PharmacyDtos.InvoiceResponse checkout(PharmacyDtos.CheckoutRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout with empty items");
        }

        User cashier = getCurrentUser();
        if (cashier == null) {
            throw new BadRequestException("Authenticated cashier required for checkout");
        }

        Patient patient = null;
        if (request.getPatientId() != null) {
            patient = patientRepository.findById(request.getPatientId()).orElse(null);
        }

        String invNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invNumber)
                .invoiceType("PHARMACY")
                .patient(patient)
                .customerName(patient != null ? patient.getFullName() : (request.getCustomerName() != null ? request.getCustomerName() : "Walk-in Customer"))
                .customerPhone(patient != null ? patient.getPhone() : request.getCustomerPhone())
                .cashier(cashier)
                .notes(request.getNotes())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalItemDiscounts = BigDecimal.ZERO;

        List<InvoiceItem> invoiceItems = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (PharmacyDtos.InvoiceItemRequest itemReq : request.getItems()) {
            Medicine med = medicineRepository.findById(itemReq.getMedicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + itemReq.getMedicineId()));

            int stripsRequired = itemReq.getQuantityStrips();
            int unitsPerStrip = med.getUnitsPerStrip();
            int baseUnitsRequired = stripsRequired * unitsPerStrip;

            // Fetch active non-expired batches ordered by FEFO with pessimistic lock
            List<MedicineBatch> batches = batchRepository.findActiveBatchesFEFOWithLock(med.getId(), today);
            int availableUnits = batches.stream().mapToInt(MedicineBatch::getQuantityInBaseUnits).sum();

            if (availableUnits < baseUnitsRequired) {
                int availableStrips = availableUnits / unitsPerStrip;
                throw new BadRequestException("Insufficient unexpired stock for " + med.getBrandName() + 
                        ". Requested: " + stripsRequired + " strips (" + baseUnitsRequired + " units), Available: " + 
                        availableStrips + " strips (" + availableUnits + " units).");
            }

            // Pricing calculations (Server authoritative)
            BigDecimal unitPrice = med.getPricePerStrip();
            BigDecimal rawLinePrice = unitPrice.multiply(BigDecimal.valueOf(stripsRequired));
            BigDecimal discount = itemReq.getDiscountAmount() != null ? itemReq.getDiscountAmount() : BigDecimal.ZERO;
            if (discount.compareTo(rawLinePrice) > 0) {
                discount = rawLinePrice;
            }

            BigDecimal taxableLineAmount = rawLinePrice.subtract(discount);
            BigDecimal taxPercent = med.getTaxPercent() != null ? med.getTaxPercent() : BigDecimal.ZERO;
            BigDecimal lineTax = taxableLineAmount.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal lineTotal = taxableLineAmount.add(lineTax);

            subtotal = subtotal.add(rawLinePrice);
            totalItemDiscounts = totalItemDiscounts.add(discount);
            totalTax = totalTax.add(lineTax);

            InvoiceItem invItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .medicine(med)
                    .itemDescription(med.getBrandName() + " (" + med.getStrength() + ")")
                    .itemType("MEDICINE")
                    .quantityStrips(stripsRequired)
                    .unitsPerStrip(unitsPerStrip)
                    .totalBaseUnits(baseUnitsRequired)
                    .unitPricePerStrip(unitPrice)
                    .discountAmount(discount)
                    .taxPercent(taxPercent)
                    .taxAmount(lineTax)
                    .lineTotal(lineTotal)
                    .build();

            // FEFO batch allocation & stock deduction
            int unitsRemainingToDeduct = baseUnitsRequired;
            for (MedicineBatch b : batches) {
                if (unitsRemainingToDeduct <= 0) break;

                int batchAvail = b.getQuantityInBaseUnits();
                int deductFromThisBatch = Math.min(batchAvail, unitsRemainingToDeduct);

                b.setQuantityInBaseUnits(batchAvail - deductFromThisBatch);
                batchRepository.save(b);

                InvoiceBatchAllocation alloc = InvoiceBatchAllocation.builder()
                        .invoiceItem(invItem)
                        .batch(b)
                        .allocatedBaseUnits(deductFromThisBatch)
                        .build();
                invItem.getBatchAllocations().add(alloc);

                // Stock Movement record for audit
                stockMovementRepository.save(StockMovement.builder()
                        .medicine(med)
                        .batch(b)
                        .movementType("SALE")
                        .quantityBaseUnits(deductFromThisBatch)
                        .referenceId(invNumber)
                        .reason("Pharmacy POS dispensing")
                        .performedBy(cashier)
                        .build());

                unitsRemainingToDeduct -= deductFromThisBatch;
            }

            invoiceItems.add(invItem);
        }

        BigDecimal invoiceDiscount = request.getInvoiceDiscount() != null ? request.getInvoiceDiscount() : BigDecimal.ZERO;
        BigDecimal grandTotal = subtotal.subtract(totalItemDiscounts).subtract(invoiceDiscount).add(totalTax);
        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        BigDecimal paid = request.getPaidAmount() != null ? request.getPaidAmount() : grandTotal;
        BigDecimal balance = grandTotal.subtract(paid);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balance = BigDecimal.ZERO;
        }

        invoice.setSubtotal(subtotal);
        invoice.setDiscountAmount(totalItemDiscounts.add(invoiceDiscount));
        invoice.setTaxAmount(totalTax);
        invoice.setTotalAmount(grandTotal);
        invoice.setPaidAmount(paid);
        invoice.setBalanceAmount(balance);
        invoice.setPaymentStatus(balance.compareTo(BigDecimal.ZERO) == 0 ? "PAID" : (paid.compareTo(BigDecimal.ZERO) > 0 ? "PARTIAL" : "UNPAID"));
        invoice.setStatus("COMPLETED");
        invoice.setItems(invoiceItems);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Record Initial Payment
        if (paid.compareTo(BigDecimal.ZERO) > 0) {
            String payCode = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Payment payment = Payment.builder()
                    .paymentCode(payCode)
                    .invoice(savedInvoice)
                    .amount(paid)
                    .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH")
                    .paymentReference(request.getPaymentReference())
                    .receivedBy(cashier)
                    .build();
            paymentRepository.save(payment);
            savedInvoice.getPayments().add(payment);
        }

        // Audit Log
        auditLogRepository.save(AuditLog.builder()
                .userId(cashier.getId())
                .username(cashier.getUsername())
                .action("DISPENSE")
                .resourceType("INVOICE")
                .resourceId(savedInvoice.getInvoiceNumber())
                .details("Completed POS sale for total: ₹" + grandTotal)
                .build());

        return mapToInvoiceResponse(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public PharmacyDtos.InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(this::mapToInvoiceResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PharmacyDtos.InvoiceResponse> filterInvoices(String query, String type, String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return invoiceRepository.filterInvoices(query, type, status, startDate, endDate, pageable).map(this::mapToInvoiceResponse);
    }

    private PharmacyDtos.SupplierDto mapToSupplierDto(Supplier s) {
        PharmacyDtos.SupplierDto dto = new PharmacyDtos.SupplierDto();
        dto.setId(s.getId());
        dto.setSupplierCode(s.getSupplierCode());
        dto.setName(s.getName());
        dto.setContactPerson(s.getContactPerson());
        dto.setPhone(s.getPhone());
        dto.setEmail(s.getEmail());
        dto.setAddress(s.getAddress());
        dto.setGstNumber(s.getGstNumber());
        dto.setIsActive(s.getIsActive());
        return dto;
    }

    private PharmacyDtos.MedicineDto mapToMedicineDto(Medicine m) {
        PharmacyDtos.MedicineDto dto = new PharmacyDtos.MedicineDto();
        dto.setId(m.getId());
        dto.setSku(m.getSku());
        dto.setBrandName(m.getBrandName());
        dto.setGenericName(m.getGenericName());
        dto.setCategoryId(m.getCategory().getId());
        dto.setCategoryName(m.getCategory().getName());
        dto.setDosageForm(m.getDosageForm());
        dto.setStrength(m.getStrength());
        dto.setManufacturer(m.getManufacturer());
        dto.setUnitsPerStrip(m.getUnitsPerStrip());
        dto.setBaseUnit(m.getBaseUnit());
        dto.setCostPerStrip(m.getCostPerStrip());
        dto.setPricePerStrip(m.getPricePerStrip());
        dto.setTaxPercent(m.getTaxPercent());
        dto.setReorderLevelStrips(m.getReorderLevelStrips());
        dto.setPrescriptionRequired(m.getPrescriptionRequired());
        dto.setIsActive(m.getIsActive());

        int availableBaseUnits = batchRepository.getTotalAvailableStockInBaseUnits(m.getId(), LocalDate.now());
        dto.setAvailableBaseUnits(availableBaseUnits);
        dto.setAvailableStockStrips(m.getUnitsPerStrip() > 0 ? availableBaseUnits / m.getUnitsPerStrip() : 0);
        return dto;
    }

    private PharmacyDtos.MedicineBatchDto mapToBatchDto(MedicineBatch b) {
        PharmacyDtos.MedicineBatchDto dto = new PharmacyDtos.MedicineBatchDto();
        dto.setId(b.getId());
        dto.setMedicineId(b.getMedicine().getId());
        dto.setMedicineBrandName(b.getMedicine().getBrandName());
        if (b.getSupplier() != null) {
            dto.setSupplierId(b.getSupplier().getId());
            dto.setSupplierName(b.getSupplier().getName());
        }
        dto.setBatchNumber(b.getBatchNumber());
        dto.setExpiryDate(b.getExpiryDate());
        dto.setManufacturingDate(b.getManufacturingDate());
        dto.setQuantityInBaseUnits(b.getQuantityInBaseUnits());
        dto.setQuantityInStrips(b.getMedicine().getUnitsPerStrip() > 0 ? b.getQuantityInBaseUnits() / b.getMedicine().getUnitsPerStrip() : 0);
        dto.setPurchaseCostPerStrip(b.getPurchaseCostPerStrip());
        dto.setMrpPerStrip(b.getMrpPerStrip());
        return dto;
    }

    private PharmacyDtos.InvoiceResponse mapToInvoiceResponse(Invoice inv) {
        PharmacyDtos.InvoiceResponse dto = new PharmacyDtos.InvoiceResponse();
        dto.setId(inv.getId());
        dto.setInvoiceNumber(inv.getInvoiceNumber());
        dto.setInvoiceType(inv.getInvoiceType());
        if (inv.getPatient() != null) dto.setPatientId(inv.getPatient().getId());
        dto.setCustomerName(inv.getCustomerName());
        dto.setCustomerPhone(inv.getCustomerPhone());
        dto.setCashierName(inv.getCashier().getFullName());
        dto.setSubtotal(inv.getSubtotal());
        dto.setDiscountAmount(inv.getDiscountAmount());
        dto.setTaxAmount(inv.getTaxAmount());
        dto.setTotalAmount(inv.getTotalAmount());
        dto.setPaidAmount(inv.getPaidAmount());
        dto.setBalanceAmount(inv.getBalanceAmount());
        dto.setPaymentStatus(inv.getPaymentStatus());
        dto.setStatus(inv.getStatus());
        dto.setNotes(inv.getNotes());
        dto.setCreatedAt(inv.getCreatedAt());

        dto.setItems(inv.getItems().stream().map(i -> {
            PharmacyDtos.InvoiceItemResponse idto = new PharmacyDtos.InvoiceItemResponse();
            idto.setId(i.getId());
            if (i.getMedicine() != null) idto.setMedicineId(i.getMedicine().getId());
            idto.setItemDescription(i.getItemDescription());
            idto.setQuantityStrips(i.getQuantityStrips());
            idto.setUnitsPerStrip(i.getUnitsPerStrip());
            idto.setTotalBaseUnits(i.getTotalBaseUnits());
            idto.setUnitPricePerStrip(i.getUnitPricePerStrip());
            idto.setDiscountAmount(i.getDiscountAmount());
            idto.setTaxPercent(i.getTaxPercent());
            idto.setTaxAmount(i.getTaxAmount());
            idto.setLineTotal(i.getLineTotal());
            idto.setBatchNumbers(i.getBatchAllocations().stream().map(a -> a.getBatch().getBatchNumber()).collect(Collectors.toList()));
            return idto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
