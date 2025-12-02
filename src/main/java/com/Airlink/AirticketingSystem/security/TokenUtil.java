package com.Airlink.AirticketingSystem.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class TokenUtil {
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate a secure random token
     */
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Get expiration time for password reset tokens (1 hour)
     */
    public Instant getPasswordResetTokenExpiration() {
        return Instant.now().plusSeconds(3600); // 1 hour
    }

    /**
     * Get expiration time for email verification tokens (24 hours)
     */
    public Instant getEmailVerificationTokenExpiration() {
        return Instant.now().plusSeconds(86400); // 24 hours
    }

    /**
     * Check if a token is expired
     */
    public boolean isTokenExpired(Instant expiryDate) {
        return Instant.now().isAfter(expiryDate);
    }
}
