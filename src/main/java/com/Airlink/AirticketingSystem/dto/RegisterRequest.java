package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.Gender;
import com.Airlink.AirticketingSystem.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;
    
    private Long locationId; // Optional
}
