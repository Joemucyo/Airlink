package com.Airlink.AirticketingSystem.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailRequest {
    @NotBlank(message = "Verification token is required")
    private String verificationToken;

    public VerifyEmailRequest() {}

    public VerifyEmailRequest(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}
