package com.medicore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @Column(name = "item_description", nullable = false, length = 200)
    private String itemDescription;

    @Column(name = "item_type", nullable = false, length = 30)
    @Builder.Default
    private String itemType = "MEDICINE"; // MEDICINE, SERVICE, FEE

    @Column(name = "quantity_strips", nullable = false)
    @Builder.Default
    private Integer quantityStrips = 1;

    @Column(name = "units_per_strip", nullable = false)
    @Builder.Default
    private Integer unitsPerStrip = 1;

    @Column(name = "total_base_units", nullable = false)
    @Builder.Default
    private Integer totalBaseUnits = 1;

    @Column(name = "unit_price_per_strip", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPricePerStrip;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_percent", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @OneToMany(mappedBy = "invoiceItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceBatchAllocation> batchAllocations = new ArrayList<>();
}
