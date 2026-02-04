package com.toycell.servicetransaction.dto;

import com.toycell.commondomain.enums.Currency;
import com.toycell.commondomain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Wallet ID is required")
    private Long walletId;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Balance before is required")
    private BigDecimal balanceBefore;

    @NotNull(message = "Balance after is required")
    private BigDecimal balanceAfter;

    private String referenceId;

    private String description;

    private Long relatedUserId; // Transfer durumunda karşı taraf
}
