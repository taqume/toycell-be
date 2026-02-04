package com.toycell.servicetransfer.client;

import com.toycell.commondomain.enums.Currency;
import com.toycell.commondomain.response.ApiResponse;
import com.toycell.servicetransfer.dto.client.FeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
        name = "fee-service",
        url = "${feign.client.fee.url}",
        configuration = FeignClientConfig.class
)
public interface FeeClient {

    @GetMapping("/api/fees/calculate/transfer")
    ApiResponse<FeeResponse> calculateTransferFee(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Currency currency
    );
}
