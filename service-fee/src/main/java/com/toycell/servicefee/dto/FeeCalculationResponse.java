package com.toycell.servicefee.dto;

import com.toycell.commondomain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ücret hesaplama response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeCalculationResponse {
    private BigDecimal originalAmount; // Orijinal tutar
    private BigDecimal feeAmount; // Ücret tutarı
    private BigDecimal totalAmount; // Toplam tutar (orijinal + ücret)
    private Currency currency;
    private String feeDetails; // Ücret hesaplama detayları
    private Long feeRuleId; // Uygulanan kural ID
}
