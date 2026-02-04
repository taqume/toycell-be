package com.toycell.servicefee.dto;

import com.toycell.commondomain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ücret hesaplama request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeCalculationRequest {
    private BigDecimal amount;
    private Currency currency;
}
