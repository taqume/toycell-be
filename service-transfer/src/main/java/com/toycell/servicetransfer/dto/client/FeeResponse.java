package com.toycell.servicetransfer.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeResponse {
    private BigDecimal feeAmount;
    private BigDecimal feePercentage;
    private String feeType;
    private String description;
}
