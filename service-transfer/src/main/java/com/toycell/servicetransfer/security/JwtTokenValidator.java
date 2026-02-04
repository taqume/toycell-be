package com.toycell.servicetransfer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtTokenValidator {

    private final SecretKey key;

    public JwtTokenValidator(@Value("${jwt.secret.key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        log.info("JWT Token Validator initialized");
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("userId", Long.class) : null;
    }
}
