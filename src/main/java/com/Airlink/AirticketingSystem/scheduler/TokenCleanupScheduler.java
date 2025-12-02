package com.Airlink.AirticketingSystem.scheduler;

import com.Airlink.AirticketingSystem.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenCleanupScheduler {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * Clean up expired refresh tokens
     * Runs daily at 2:00 AM (0 0 2 * * ?)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        try {
            // Delete all refresh tokens that expired before now
            refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        } catch (Exception e) {
            // Log the error but don't throw - scheduler should continue running
            System.err.println("Error during token cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
