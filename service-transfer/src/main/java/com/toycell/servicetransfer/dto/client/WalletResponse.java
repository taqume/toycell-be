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
public class WalletResponse {
    private Long id;
    private Long userId;
    private Currency currency;
    private BigDecimal balance;
    private Boolean active;
}
