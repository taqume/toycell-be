package com.toycell.commonexception.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Authentication & Authorization
    AUTH_FAILED("AUTH_001", "Authentication failed"),
    INVALID_TOKEN("AUTH_002", "Invalid or expired token"),
    USER_ALREADY_EXISTS("AUTH_003", "User already exists"),
    USER_NOT_FOUND("AUTH_004", "User not found"),
    INVALID_CREDENTIALS("AUTH_005", "Invalid email or password"),
    
    // Account
    PROFILE_NOT_FOUND("ACC_001", "User profile not found"),
    PROFILE_ALREADY_EXISTS("ACC_002", "User profile already exists"),
    
    // Balance
    WALLET_NOT_FOUND("BAL_001", "Wallet not found"),
    WALLET_ALREADY_EXISTS("BAL_002", "Wallet already exists for this currency"),
    INSUFFICIENT_BALANCE("BAL_003", "Insufficient balance"),
    WALLET_INACTIVE("BAL_004", "Wallet is inactive"),
    INVALID_AMOUNT("BAL_005", "Invalid amount"),
    
    // Fee
    FEE_CALCULATION_FAILED("FEE_001", "Fee calculation failed"),
    FEE_RULE_NOT_FOUND("FEE_002", "No active fee rule found"),
    
    // Transfer
    TRANSFER_FAILED("TRF_001", "Transfer operation failed"),
    TRANSFER_NOT_FOUND("TRF_002", "Transfer not found"),
    SAME_USER_TRANSFER("TRF_003", "Cannot transfer to same user"),
    CURRENCY_MISMATCH("TRF_004", "Currency mismatch"),
    
    // Transaction
    TRANSACTION_NOT_FOUND("TXN_001", "Transaction not found"),
    
    // Validation
    VALIDATION_ERROR("VAL_001", "Validation error"),
    
    // General
    INTERNAL_SERVER_ERROR("GEN_001", "Internal server error"),
    SERVICE_UNAVAILABLE("GEN_002", "Service temporarily unavailable"),
    RESOURCE_NOT_FOUND("GEN_003", "Resource not found");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
