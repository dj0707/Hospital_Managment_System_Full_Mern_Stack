package com.medicore.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "brand_name", nullable = false, length = 150)
    private String brandName;

    @Column(name = "generic_name", nullable = false, length = 150)
    private String genericName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private MedicineCategory category;

    @Column(name = "dosage_form", nullable = false, length = 50)
    private String dosageForm; // Tablet, Capsule, Syrup, Injection, Ointment

    @Column(nullable = false, length = 50)
    private String strength; // 500mg, 10ml, etc.

    @Column(nullable = false, length = 150)
    private String manufacturer;

    @Column(name = "units_per_strip", nullable = false)
    @Builder.Default
    private Integer unitsPerStrip = 10;

    @Column(name = "base_unit", nullable = false, length = 30)
    @Builder.Default
    private String baseUnit = "Tablet";

    @Column(name = "cost_per_strip", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal costPerStrip = BigDecimal.ZERO;

    @Column(name = "price_per_strip", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pricePerStrip = BigDecimal.ZERO;

    @Column(name = "tax_percent", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Column(name = "reorder_level_strips", nullable = false)
    @Builder.Default
    private Integer reorderLevelStrips = 10;

    @Column(name = "prescription_required", nullable = false)
    @Builder.Default
    private Boolean prescriptionRequired = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
