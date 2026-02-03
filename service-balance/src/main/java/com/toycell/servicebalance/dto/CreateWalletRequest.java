package com.toycell.servicebalance.dto;

import com.toycell.commondomain.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalletRequest {
    
    @NotNull(message = "Currency is required")
    private Currency currency;
}
