package com.toycell.servicetransfer.client;

import com.toycell.commondomain.response.ApiResponse;
import com.toycell.servicetransfer.dto.client.DepositRequest;
import com.toycell.servicetransfer.dto.client.WalletResponse;
import com.toycell.servicetransfer.dto.client.WithdrawRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "balance-service",
        url = "${feign.client.balance.url}",
        configuration = FeignClientConfig.class
)
public interface BalanceClient {

    @GetMapping("/api/wallets/{walletId}")
    ApiResponse<WalletResponse> getWallet(@PathVariable("walletId") Long walletId);

    @PostMapping("/api/wallets/deposit")
    ApiResponse<WalletResponse> deposit(@RequestBody DepositRequest request);

    @PostMapping("/api/wallets/withdraw")
    ApiResponse<WalletResponse> withdraw(@RequestBody WithdrawRequest request);
}
