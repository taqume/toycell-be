package com.toycell.servicetransaction.controller;

import com.toycell.commondomain.enums.Currency;
import com.toycell.commondomain.enums.TransactionType;
import com.toycell.commondomain.response.ApiResponse;
import com.toycell.servicetransaction.dto.TransactionRequest;
import com.toycell.servicetransaction.dto.TransactionResponse;
import com.toycell.servicetransaction.dto.TransactionStatisticsResponse;
import com.toycell.servicetransaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Yeni bir transaction kaydı oluşturur
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.ok(ApiResponse.success("Transaction created successfully", response));
    }

    /**
     * Transaction ID'ye göre işlemi getirir
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(@PathVariable Long id) {
        TransactionResponse response = transactionService.getTransaction(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kullanıcının tüm işlemlerini getirir
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> response = transactionService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kullanıcının belirli bir cüzdanındaki işlemlerini getirir
     */
    @GetMapping("/user/{userId}/wallet/{walletId}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getWalletTransactions(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> response = transactionService.getWalletTransactions(
                userId, walletId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kullanıcının belirli türdeki işlemlerini getirir
     */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getUserTransactionsByType(
            @PathVariable Long userId,
            @PathVariable TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> response = transactionService.getUserTransactionsByType(
                userId, type, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kullanıcının belirli para birimindeki işlemlerini getirir
     */
    @GetMapping("/user/{userId}/currency/{currency}")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getUserTransactionsByCurrency(
            @PathVariable Long userId,
            @PathVariable Currency currency,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> response = transactionService.getUserTransactionsByCurrency(
                userId, currency, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Tarih aralığında işlemleri getirir
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getUserTransactionsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> response = transactionService.getUserTransactionsByDateRange(
                userId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Reference ID'ye göre işlemleri getirir (Transfer için)
     */
    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByReference(
            @PathVariable String referenceId) {
        List<TransactionResponse> response = transactionService.getTransactionsByReference(referenceId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kullanıcının işlem istatistiklerini getirir
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<ApiResponse<TransactionStatisticsResponse>> getUserStatistics(
            @PathVariable Long userId) {
        TransactionStatisticsResponse response = transactionService.getUserStatistics(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Tarih aralığında kullanıcının istatistiklerini getirir
     */
    @GetMapping("/user/{userId}/statistics/date-range")
    public ResponseEntity<ApiResponse<TransactionStatisticsResponse>> getUserStatisticsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        TransactionStatisticsResponse response = transactionService.getUserStatisticsByDateRange(
                userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Transaction service is running"));
    }
}
