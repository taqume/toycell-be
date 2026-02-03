package com.toycell.commonencrypt.util;

import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commonexception.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class AesEncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final int IV_SIZE = 16; // 16 bytes for AES

    private final SecretKey secretKey;

    public AesEncryptionUtil(@Value("${encryption.secret.key:ToycellSecretKey2026______}") String secretKeyString) {
        // Ensure key is exactly 32 bytes for AES-256
        byte[] keyBytes = ensureKeyLength(secretKeyString);
        this.secretKey = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        log.info("AES-256 encryption initialized");
    }

    /**
     * Encrypts the given plain text using AES-256-CBC with a randomly generated IV.
     * The IV is prepended to the encrypted data and returned as a Base64 string.
     *
     * @param plainText the text to encrypt
     * @return Base64-encoded string with format: [IV][EncryptedData]
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            // Generate random IV
            byte[] iv = generateIv();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // Encrypt the data
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            // Prepend IV to encrypted data
            byte[] combined = new byte[IV_SIZE + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, IV_SIZE);
            System.arraycopy(encryptedBytes, 0, combined, IV_SIZE, encryptedBytes.length);

            // Return as Base64
            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Encryption failed");
        }
    }

    /**
     * Decrypts the given Base64-encoded encrypted text.
     * Expects format: [IV][EncryptedData]
     *
     * @param encryptedText Base64-encoded encrypted string
     * @return decrypted plain text
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(encryptedText);

            // Extract IV (first 16 bytes)
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Extract encrypted data
            byte[] encryptedBytes = new byte[combined.length - IV_SIZE];
            System.arraycopy(combined, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // Decrypt
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);

        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Decryption failed");
        }
    }

    private byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private byte[] ensureKeyLength(String key) {
        byte[] keyBytes = new byte[32]; // 32 bytes = 256 bits
        byte[] originalBytes = key.getBytes();
        
        // Copy up to 32 bytes, pad with zeros if shorter
        System.arraycopy(originalBytes, 0, keyBytes, 0, 
                Math.min(originalBytes.length, 32));
        
        return keyBytes;
    }
}
