package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.Gender;
import com.Airlink.AirticketingSystem.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserRequestDTO {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    @NotNull(message = "Role is required")
    private UserRole role;

    private Long locationId;

    @NotBlank(message = "Password is required")
    private String password;

    // Default constructor
    public UserRequestDTO() {}    // Constructor with all fields
    public UserRequestDTO(String fullName, String email, String phone, Gender gender, UserRole role, Long locationId, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
        this.locationId = locationId;
        this.password = password;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}