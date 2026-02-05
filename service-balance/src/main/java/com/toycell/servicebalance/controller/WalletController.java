package com.toycell.servicebalance.controller;

import com.toycell.commondomain.response.ApiResponse;
import com.toycell.servicebalance.dto.*;
import com.toycell.servicebalance.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @Valid @RequestBody CreateWalletRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        WalletResponse wallet = walletService.createWallet(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet created successfully", wallet));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getMyWallets(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<WalletResponse> wallets = walletService.getUserWallets(userId);
        return ResponseEntity.ok(ApiResponse.success("Wallets retrieved successfully", wallets));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(
            @PathVariable Long walletId,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        WalletResponse wallet = walletService.getWallet(walletId, userId);
        return ResponseEntity.ok(ApiResponse.success("Wallet retrieved successfully", wallet));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        TransactionResponse transaction = walletService.deposit(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        TransactionResponse transaction = walletService.withdraw(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", transaction));
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getWalletTransactions(
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<TransactionResponse> transactions = walletService.getWalletTransactions(
                walletId, userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<TransactionResponse> transactions = walletService.getUserTransactions(
                userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Balance Service is running"));
    }

    // Internal endpoints for service-to-service communication
    @GetMapping("/internal/{walletId}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletInternal(@PathVariable Long walletId) {
        WalletResponse wallet = walletService.getWalletInternal(walletId);
        return ResponseEntity.ok(ApiResponse.success("Wallet retrieved successfully", wallet));
    }

    @PostMapping("/internal/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> depositInternal(
            @Valid @RequestBody DepositRequest request) {
        TransactionResponse transaction = walletService.depositInternal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", transaction));
    }

    @PostMapping("/internal/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdrawInternal(
            @Valid @RequestBody WithdrawRequest request) {
        TransactionResponse transaction = walletService.withdrawInternal(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", transaction));
    }
}
