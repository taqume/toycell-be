package com.toycell.servicetransaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatisticsResponse {

    private Long totalTransactions;
    private Long depositCount;
    private Long withdrawCount;
    private Long transferInCount;
    private Long transferOutCount;
    
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal totalTransfersIn;
    private BigDecimal totalTransfersOut;
    
    private BigDecimal netBalance; // deposits + transfersIn - withdrawals - transfersOut
}
