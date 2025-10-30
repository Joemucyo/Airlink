package com.Airlink.AirticketingSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.Airlink.AirticketingSystem.model.enums.AirportType;

public class AirportRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "IATA code is required")
    @Size(min = 3, max = 3, message = "IATA code must be exactly 3 characters")
    private String code;

    private String description;

    @NotNull(message = "Airport type is required")
    private AirportType type;

    // Location fields
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @Size(min = 3, max = 3, message = "IATA city code must be 3 characters")
    private String iataCityCode;
    
    private Double latitude;
    private Double longitude;
    
    private String timezone;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AirportType getType() { return type; }
    public void setType(AirportType type) { this.type = type; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getIataCityCode() { return iataCityCode; }
    public void setIataCityCode(String iataCityCode) { this.iataCityCode = iataCityCode; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}

