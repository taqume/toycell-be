package com.toycell.serviceauth.service;

import com.toycell.commonexception.enums.ErrorCode;
import com.toycell.commonexception.exception.BusinessException;
import com.toycell.serviceauth.dto.AuthResponse;
import com.toycell.serviceauth.dto.LoginRequest;
import com.toycell.serviceauth.dto.RegisterRequest;
import com.toycell.serviceauth.entity.User;
import com.toycell.serviceauth.repository.UserRepository;
import com.toycell.serviceauth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CaptchaService captchaService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Email already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "Username already taken");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );

        return AuthResponse.of(
                token,
                jwtTokenProvider.getExpirationMs(),
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Check if captcha is required (after previous failed attempt)
        if (captchaService.isCaptchaRequired(request.getEmail())) {
            if (request.getCaptchaId() == null || request.getCaptchaId().isBlank() ||
                request.getCaptchaAnswer() == null || request.getCaptchaAnswer().isBlank()) {
                throw new BusinessException(ErrorCode.CAPTCHA_VALIDATION_FAILED, "CAPTCHA is required after a failed login attempt.");
            }
            captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptchaAnswer());
        }

        try {
            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

            // Check if user is active
            if (!user.getActive()) {
                throw new BusinessException(ErrorCode.AUTH_FAILED, "Account is inactive");
            }

            // Validate password
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
            }

            // Success - clear failed attempts
            captchaService.clearFailedAttempts(request.getEmail());
            log.info("User logged in successfully: {}", user.getEmail());

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRole().name()
            );

            return AuthResponse.of(
                    token,
                    jwtTokenProvider.getExpirationMs(),
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
        } catch (BusinessException e) {
            captchaService.recordFailedAttempt(request.getEmail());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
