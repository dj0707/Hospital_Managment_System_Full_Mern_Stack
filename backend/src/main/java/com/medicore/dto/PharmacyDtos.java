package com.medicore.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PharmacyDtos {

    @Data
    public static class CategoryDto {
        private Long id;
        @NotBlank(message = "Category name is required")
        private String name;
        private String description;
    }

    @Data
    public static class SupplierDto {
        private Long id;
        private String supplierCode;
        @NotBlank(message = "Supplier name is required")
        private String name;
        private String contactPerson;
        @NotBlank(message = "Phone is required")
        private String phone;
        private String email;
        private String address;
        private String gstNumber;
        private Boolean isActive;
    }

    @Data
    public static class MedicineDto {
        private Long id;
        @NotBlank(message = "SKU is required")
        private String sku;
        @NotBlank(message = "Brand name is required")
        private String brandName;
        @NotBlank(message = "Generic name is required")
        private String genericName;
        @NotNull(message = "Category ID is required")
        private Long categoryId;
        private String categoryName;
        @NotBlank(message = "Dosage form is required")
        private String dosageForm;
        @NotBlank(message = "Strength is required")
        private String strength;
        @NotBlank(message = "Manufacturer is required")
        private String manufacturer;
        @Min(value = 1, message = "Units per strip must be at least 1")
        private Integer unitsPerStrip = 10;
        private String baseUnit = "Tablet";
        @NotNull(message = "Cost per strip is required")
        private BigDecimal costPerStrip;
        @NotNull(message = "Selling price per strip is required")
        private BigDecimal pricePerStrip;
        private BigDecimal taxPercent = BigDecimal.ZERO;
        private Integer reorderLevelStrips = 10;
        private Boolean prescriptionRequired = false;
        private Boolean isActive = true;
        private Integer availableStockStrips;
        private Integer availableBaseUnits;
    }

    @Data
    public static class MedicineBatchDto {
        private Long id;
        @NotNull(message = "Medicine ID is required")
        private Long medicineId;
        private String medicineBrandName;
        private Long supplierId;
        private String supplierName;
        @NotBlank(message = "Batch number is required")
        private String batchNumber;
        @NotNull(message = "Expiry date is required")
        private LocalDate expiryDate;
        private LocalDate manufacturingDate;
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantityInBaseUnits;
        private Integer quantityInStrips;
        private BigDecimal purchaseCostPerStrip;
        private BigDecimal mrpPerStrip;
    }

    @Data
    public static class StockAdjustmentDto {
        @NotNull(message = "Medicine ID is required")
        private Long medicineId;
        @NotNull(message = "Batch ID is required")
        private Long batchId;
        @NotNull(message = "Quantity in base units is required")
        private Integer quantityBaseUnits;
        @NotBlank(message = "Movement type is required (ADJUSTMENT_ADD, ADJUSTMENT_DEDUCT, EXPIRED_DISPOSAL)")
        private String movementType;
        @NotBlank(message = "Reason is mandatory for stock adjustment")
        private String reason;
    }

    @Data
    public static class InvoiceItemRequest {
        @NotNull(message = "Medicine ID is required")
        private Long medicineId;
        @Min(value = 1, message = "Quantity strips must be at least 1")
        private Integer quantityStrips;
        private BigDecimal discountAmount = BigDecimal.ZERO;
    }

    @Data
    public static class CheckoutRequest {
        private Long patientId;
        private String customerName;
        private String customerPhone;
        @NotEmpty(message = "Checkout requires at least one medicine item")
        private List<InvoiceItemRequest> items;
        private BigDecimal invoiceDiscount = BigDecimal.ZERO;
        @NotNull(message = "Paid amount is required")
        private BigDecimal paidAmount;
        @NotBlank(message = "Payment method is required (CASH, UPI, CARD, NET_BANKING)")
        private String paymentMethod;
        private String paymentReference;
        private String notes;
        private String idempotencyKey;
    }

    @Data
    public static class InvoiceItemResponse {
        private Long id;
        private Long medicineId;
        private String itemDescription;
        private Integer quantityStrips;
        private Integer unitsPerStrip;
        private Integer totalBaseUnits;
        private BigDecimal unitPricePerStrip;
        private BigDecimal discountAmount;
        private BigDecimal taxPercent;
        private BigDecimal taxAmount;
        private BigDecimal lineTotal;
        private List<String> batchNumbers;
    }

    @Data
    public static class InvoiceResponse {
        private Long id;
        private String invoiceNumber;
        private String invoiceType;
        private Long patientId;
        private String customerName;
        private String customerPhone;
        private String cashierName;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount;
        private String paymentStatus;
        private String status;
        private String notes;
        private java.time.LocalDateTime createdAt;
        private List<InvoiceItemResponse> items;
    }
}
