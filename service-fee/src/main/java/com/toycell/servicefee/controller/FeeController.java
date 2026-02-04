package com.toycell.servicefee.controller;

import com.toycell.commondomain.response.ApiResponse;
import com.toycell.commondomain.enums.Currency;
import com.toycell.servicefee.dto.FeeCalculationRequest;
import com.toycell.servicefee.dto.FeeCalculationResponse;
import com.toycell.servicefee.dto.FeeRuleRequest;
import com.toycell.servicefee.dto.FeeRuleResponse;
import com.toycell.servicefee.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ücret hesaplama ve kural yönetimi controller
 */
@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    /**
     * Transfer ücreti hesaplar
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<FeeCalculationResponse>> calculateFee(
            @Valid @RequestBody FeeCalculationRequest request) {
        FeeCalculationResponse response = feeService.calculateFee(request);
        return ResponseEntity.ok(ApiResponse.success("Fee calculated successfully", response));
    }

    /**
     * Yeni ücret kuralı oluşturur (ADMIN)
     */
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<FeeRuleResponse>> createFeeRule(
            @Valid @RequestBody FeeRuleRequest request) {
        FeeRuleResponse response = feeService.createFeeRule(request);
        return ResponseEntity.ok(ApiResponse.success("Fee rule created successfully", response));
    }

    /**
     * Ücret kuralını günceller (ADMIN)
     */
    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<FeeRuleResponse>> updateFeeRule(
            @PathVariable Long id,
            @Valid @RequestBody FeeRuleRequest request) {
        FeeRuleResponse response = feeService.updateFeeRule(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fee rule updated successfully", response));
    }

    /**
     * Ücret kuralını siler (ADMIN)
     */
    @DeleteMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeeRule(@PathVariable Long id) {
        feeService.deleteFeeRule(id);
        return ResponseEntity.ok(ApiResponse.success("Fee rule deleted successfully", null));
    }

    /**
     * ID'ye göre ücret kuralını getirir
     */
    @GetMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<FeeRuleResponse>> getFeeRule(@PathVariable Long id) {
        FeeRuleResponse response = feeService.getFeeRule(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Para birimine göre aktif kuralları getirir
     */
    @GetMapping("/rules/currency/{currency}")
    public ResponseEntity<ApiResponse<List<FeeRuleResponse>>> getFeeRulesByCurrency(
            @PathVariable Currency currency) {
        List<FeeRuleResponse> responses = feeService.getFeeRulesByCurrency(currency);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Tüm aktif kuralları getirir
     */
    @GetMapping("/rules/active")
    public ResponseEntity<ApiResponse<List<FeeRuleResponse>>> getAllActiveFeeRules() {
        List<FeeRuleResponse> responses = feeService.getAllActiveFeeRules();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Tüm kuralları getirir (ADMIN)
     */
    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<FeeRuleResponse>>> getAllFeeRules() {
        List<FeeRuleResponse> responses = feeService.getAllFeeRules();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Fee service is running"));
    }
}
