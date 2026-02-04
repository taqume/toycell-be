package com.toycell.servicetransfer.controller;

import com.toycell.commondomain.response.ApiResponse;
import com.toycell.servicetransfer.dto.TransferRequest;
import com.toycell.servicetransfer.dto.TransferResponse;
import com.toycell.servicetransfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

    private final TransferService transferService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Transfer Service is running", "OK"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        
        Long userId = (Long) authentication.getPrincipal();
        log.info("Transfer request from user {}: {} {} from wallet {} to {}",
                userId, request.getAmount(), request.getCurrency(),
                request.getSenderWalletId(), request.getReceiverWalletId());

        TransferResponse response = transferService.transfer(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(
                "Transfer completed successfully",
                response
        ));
    }
}
