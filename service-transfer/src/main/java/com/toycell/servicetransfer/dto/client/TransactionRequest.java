package com.toycell.servicetransfer.dto.client;

import com.toycell.commondomain.enums.Currency;
import com.toycell.commondomain.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private Long userId;
    private Long walletId;
    private TransactionType type;
    private BigDecimal amount;
    private Currency currency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceId;
    private Long relatedUserId;
}
