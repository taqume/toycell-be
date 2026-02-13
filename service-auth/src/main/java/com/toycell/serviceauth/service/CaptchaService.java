package com.toycell.serviceauth.service;

import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commonexception.exception.BusinessException;
import com.toycell.serviceauth.dto.CaptchaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class CaptchaService {

    private static final long CAPTCHA_TTL_MS = 5 * 60 * 1000; // 5 minutes

    private record CaptchaEntry(int answer, long createdAt) {}

    private final Map<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    /**
     * Returns true if this email has at least 1 failed login attempt.
     */
    public boolean isCaptchaRequired(String email) {
        return failedAttempts.getOrDefault(email.toLowerCase(), 0) >= 1;
    }

    /**
     * Records a failed login attempt for the given email.
     */
    public void recordFailedAttempt(String email) {
        failedAttempts.merge(email.toLowerCase(), 1, Integer::sum);
        log.debug("Failed attempts for {}: {}", email, failedAttempts.get(email.toLowerCase()));
    }

    /**
     * Clears failed login attempts on successful login.
     */
    public void clearFailedAttempts(String email) {
        failedAttempts.remove(email.toLowerCase());
    }

    /**
     * Generates a random math captcha (addition, subtraction, or multiplication).
     * Returns a captchaId + question string.
     */
    public CaptchaResponse generateCaptcha() {
        int a, b, answer;
        String question;
        int op = (int) (Math.random() * 3);

        switch (op) {
            case 0 -> { // addition
                a = (int) (Math.random() * 50) + 1;
                b = (int) (Math.random() * 50) + 1;
                answer = a + b;
                question = a + " + " + b + " = ?";
            }
            case 1 -> { // subtraction (ensure positive result)
                a = (int) (Math.random() * 50) + 20;
                b = (int) (Math.random() * 20) + 1;
                answer = a - b;
                question = a + " - " + b + " = ?";
            }
            default -> { // multiplication
                a = (int) (Math.random() * 10) + 1;
                b = (int) (Math.random() * 10) + 1;
                answer = a * b;
                question = a + " × " + b + " = ?";
            }
        }

        String captchaId = UUID.randomUUID().toString();
        captchaStore.put(captchaId, new CaptchaEntry(answer, System.currentTimeMillis()));

        log.debug("Generated captcha [{}]: {} (answer: {})", captchaId, question, answer);

        return CaptchaResponse.builder()
                .captchaId(captchaId)
                .question(question)
                .build();
    }

    /**
     * Verifies the user's captcha answer against the stored answer.
     * Each captcha can only be used once (removed after verification attempt).
     *
     * @param captchaId  the captcha identifier
     * @param userAnswer the user's answer
     * @throws BusinessException if verification fails
     */
    public void verifyCaptcha(String captchaId, String userAnswer) {
        CaptchaEntry entry = captchaStore.remove(captchaId);

        if (entry == null) {
            log.warn("CAPTCHA not found or already used: {}", captchaId);
            throw new BusinessException(ErrorCode.CAPTCHA_VALIDATION_FAILED, "CAPTCHA expired or invalid. Please refresh and try again.");
        }

        // Check TTL
        if (System.currentTimeMillis() - entry.createdAt() > CAPTCHA_TTL_MS) {
            log.warn("CAPTCHA expired: {}", captchaId);
            throw new BusinessException(ErrorCode.CAPTCHA_VALIDATION_FAILED, "CAPTCHA expired. Please refresh and try again.");
        }

        // Check answer
        try {
            int parsed = Integer.parseInt(userAnswer.trim());
            if (parsed != entry.answer()) {
                log.warn("CAPTCHA answer mismatch for [{}]: expected={}, got={}", captchaId, entry.answer(), parsed);
                throw new BusinessException(ErrorCode.CAPTCHA_VALIDATION_FAILED, "Incorrect CAPTCHA answer.");
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.CAPTCHA_VALIDATION_FAILED, "Invalid CAPTCHA answer format.");
        }

        log.debug("CAPTCHA verified successfully: {}", captchaId);
    }

    /**
     * Cleanup expired captchas every 10 minutes.
     */
    @Scheduled(fixedRate = 600_000)
    public void cleanupExpiredCaptchas() {
        long now = System.currentTimeMillis();
        int removed = 0;
        var iterator = captchaStore.entrySet().iterator();
        while (iterator.hasNext()) {
            if (now - iterator.next().getValue().createdAt() > CAPTCHA_TTL_MS) {
                iterator.remove();
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("Cleaned up {} expired captchas. Remaining: {}", removed, captchaStore.size());
        }
    }
}
