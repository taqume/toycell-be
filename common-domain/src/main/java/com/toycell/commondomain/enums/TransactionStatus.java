package com.toycell.commondomain.enums;

public enum TransactionStatus {
    PENDING("Pending"),
    SUCCESS("Successful"),
    FAILED("Failed"),
    ROLLED_BACK("Rolled Back");

    private final String displayName;

    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
