package com.toycell.serviceaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String identityNumber; // Decrypted for display (mask it in real scenario)
    private String phoneNumber; // Decrypted
    private LocalDate birthDate;
    private Boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getMaskedIdentityNumber() {
        if (identityNumber == null || identityNumber.length() < 4) {
            return "***";
        }
        return "*******" + identityNumber.substring(identityNumber.length() - 4);
    }

    public String getMaskedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 2);
    }
}
