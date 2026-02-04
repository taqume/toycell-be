package com.toycell.servicetransfer.client;

import com.toycell.commondomain.response.ApiResponse;
import com.toycell.servicetransfer.dto.client.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "transaction-service",
        url = "${feign.client.transaction.url}",
        configuration = FeignClientConfig.class
)
public interface TransactionClient {

    @PostMapping("/api/transactions")
    ApiResponse<Object> createTransaction(@RequestBody TransactionRequest request);
}
