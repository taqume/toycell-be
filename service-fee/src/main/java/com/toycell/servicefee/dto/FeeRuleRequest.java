package com.toycell.servicefee.dto;

import com.toycell.commondomain.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Ücret kuralı oluşturma/güncelleme request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeRuleRequest {

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.0", message = "Minimum amount must be greater than or equal to 0")
    private BigDecimal minAmount;

    @DecimalMin(value = "0.0", message = "Maximum amount must be greater than or equal to 0")
    private BigDecimal maxAmount;

    @NotNull(message = "Fee percentage is required")
    @DecimalMin(value = "0.0", message = "Fee percentage must be greater than or equal to 0")
    @DecimalMax(value = "100.0", message = "Fee percentage must be less than or equal to 100")
    private BigDecimal feePercentage;

    @NotNull(message = "Fixed fee is required")
    @DecimalMin(value = "0.0", message = "Fixed fee must be greater than or equal to 0")
    private BigDecimal fixedFee;

    @NotNull(message = "Minimum fee is required")
    @DecimalMin(value = "0.0", message = "Minimum fee must be greater than or equal to 0")
    private BigDecimal minFee;

    @DecimalMin(value = "0.0", message = "Maximum fee must be greater than or equal to 0")
    private BigDecimal maxFee;

    private Boolean active = true;

    @Min(value = 0, message = "Rule priority must be greater than or equal to 0")
    private Integer rulePriority = 0;
}
