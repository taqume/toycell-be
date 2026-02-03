package com.toycell.servicebalance.dto;

import com.toycell.commondomain.enums.Currency;
import com.toycell.servicebalance.entity.BalanceTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long walletId;
    private Long userId;
    private BalanceTransaction.TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private Currency currency;
    private String description;
    private String referenceId;
    private LocalDateTime createdAt;
}
