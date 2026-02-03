package com.toycell.commondomain.enums;

public enum Currency {
    TRY("Turkish Lira"),
    USD("US Dollar"),
    EUR("Euro");

    private final String displayName;

    Currency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
