package com.toycell.servicetransfer.dto.client;

import com.toycell.commondomain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {
    private Long walletId;
    private BigDecimal amount;
    private Currency currency;
    private String description;
}
