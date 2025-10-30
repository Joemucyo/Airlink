package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.AirportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirportResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private AirportType type;
    
    // Location information
    private String city;
    private String country;
    private String iataCityCode;
    private Double latitude;
    private Double longitude;
    private String timezone;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Flight counts
    private Integer departingFlightsCount;
    private Integer arrivingFlightsCount;
    
    // Helper method to get full location string
    public String getFullLocation() {
        return String.format("%s, %s (%s)", 
            city, 
            country, 
            iataCityCode != null ? iataCityCode : code
        );
    }
    
    // Helper method to get coordinates
    public String getCoordinates() {
        if (latitude != null && longitude != null) {
            return String.format("%.6f, %.6f", latitude, longitude);
        }
        return null;
    }
}

