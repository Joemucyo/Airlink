package com.Airlink.AirticketingSystem.controller;

import com.Airlink.AirticketingSystem.dto.AuthRegisterRequest;
import com.Airlink.AirticketingSystem.dto.AuthResponse;
import com.Airlink.AirticketingSystem.dto.ChangePasswordRequest;
import com.Airlink.AirticketingSystem.dto.ForgotPasswordRequest;
import com.Airlink.AirticketingSystem.dto.LoginRequest;
import com.Airlink.AirticketingSystem.dto.RefreshTokenRequest;
import com.Airlink.AirticketingSystem.dto.ResendVerificationRequest;
import com.Airlink.AirticketingSystem.dto.ResetPasswordRequest;
import com.Airlink.AirticketingSystem.dto.UserResponseDTO;
import com.Airlink.AirticketingSystem.dto.VerifyEmailRequest;
import com.Airlink.AirticketingSystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        UserResponseDTO user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        authService.changePassword(email, request);
        return ResponseEntity.noContent().build();
    }

    // ============ Password Reset Endpoints ============

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password has been reset successfully");
    }

    // ============ Email Verification Endpoints ============

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok("Email has been verified successfully");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.ok("Verification email has been sent");
    }
}
