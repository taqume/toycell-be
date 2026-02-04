package com.toycell.servicetransfer.dto;

import com.toycell.commondomain.enums.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Sender wallet ID is required")
    private Long senderWalletId;

    @NotNull(message = "Receiver wallet ID is required")
    private Long receiverWalletId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    private Currency currency;

    private String description;
}
