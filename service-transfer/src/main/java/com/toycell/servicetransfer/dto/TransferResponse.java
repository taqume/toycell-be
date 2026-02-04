package com.toycell.servicetransfer.dto;

import com.toycell.commondomain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private Long transferId;
    private Long senderWalletId;
    private Long receiverWalletId;
    private BigDecimal amount;
    private Currency currency;
    private BigDecimal feeAmount;
    private BigDecimal totalAmount;
    private String description;
    private LocalDateTime transferDate;
    private Long senderTransactionId;
    private Long receiverTransactionId;
}
