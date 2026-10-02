package com.medicore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false, length = 100)
    private String dosage; // 1-0-1

    @Column(nullable = false, length = 100)
    private String frequency; // After food

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "quantity_strips", nullable = false)
    @Builder.Default
    private Integer quantityStrips = 1;

    @Column(columnDefinition = "TEXT")
    private String instructions;
}
