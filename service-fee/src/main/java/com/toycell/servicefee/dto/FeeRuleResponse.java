package com.toycell.servicefee.dto;

import com.toycell.commondomain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ücret kuralı response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeRuleResponse {
    private Long id;
    private Currency currency;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal feePercentage;
    private BigDecimal fixedFee;
    private BigDecimal minFee;
    private BigDecimal maxFee;
    private Boolean active;
    private Integer rulePriority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
