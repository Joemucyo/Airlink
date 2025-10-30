package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationRequestDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String code; // Optional - will be auto-generated if not provided
    
    @NotNull(message = "Type is required")
    private LocationType type;
    
    private Long parentId;
    
    // Default constructor
    public LocationRequestDTO() {}
    
    // Constructor with all fields
    public LocationRequestDTO(String name, String code, LocationType type, Long parentId) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.parentId = parentId;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocationType getType() {
        return type;
    }
    
    public void setType(LocationType type) {
        this.type = type;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
