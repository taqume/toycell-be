package com.toycell.servicefee.entity;

import com.toycell.commondomain.entity.BaseEntity;
import com.toycell.commondomain.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Ücret kurallarını tanımlayan entity
 */
@Entity
@Table(name = "fee_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeRule extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    @Column(name = "min_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal minAmount; // Minimum işlem tutarı

    @Column(name = "max_amount", precision = 19, scale = 2)
    private BigDecimal maxAmount; // Maximum işlem tutarı (null = sınırsız)

    @Column(name = "fee_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal feePercentage; // Yüzde olarak ücret (örn: 1.5)

    @Column(name = "fixed_fee", precision = 19, scale = 2, nullable = false)
    private BigDecimal fixedFee; // Sabit ücret

    @Column(name = "min_fee", precision = 19, scale = 2, nullable = false)
    private BigDecimal minFee; // Minimum ücret

    @Column(name = "max_fee", precision = 19, scale = 2)
    private BigDecimal maxFee; // Maximum ücret (null = sınırsız)

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "rule_priority")
    private Integer rulePriority = 0; // Öncelik sırası
}
