package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.FareClass;
import com.Airlink.AirticketingSystem.model.enums.FlightType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public class FlightRequestDTO {
    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotBlank(message = "Airline is required")
    private String airline;

    @NotNull(message = "Flight type is required")
    private FlightType flightType;

    // For domestic flights
    private Long originLocationId;
    private Long destinationLocationId;
    
    // For international flights
    private Long originAirportId;
    private Long destinationAirportId;

    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be greater than 0")
    private Double price;

    @NotNull(message = "Total capacity is required")
    @Positive(message = "Total capacity must be greater than 0")
    private Integer totalCapacity;

    @NotNull(message = "Available seats is required")
    @PositiveOrZero(message = "Available seats cannot be negative")
    private Integer availableSeats;

    // Optional fare class price overrides
    @Valid
    private Map<FareClass, @NotNull @Positive Double> fareClassPrices = new EnumMap<>(FareClass.class);

    // Required if fareClassPrices is provided
    @Valid
    private Map<FareClass, @NotNull @PositiveOrZero Integer> availableSeatsPerClass = new EnumMap<>(FareClass.class);

    // Validation method to ensure proper configuration based on flight type
    @AssertTrue(message = "For domestic flights, origin and destination location IDs are required")
    private boolean isValidDomesticFlight() {
        if (flightType == FlightType.DOMESTIC) {
            return originLocationId != null && destinationLocationId != null;
        }
        return true;
    }
    
    @AssertTrue(message = "For international flights, origin and destination airport IDs are required")
    private boolean isValidInternationalFlight() {
        if (flightType == FlightType.INTERNATIONAL) {
            return originAirportId != null && destinationAirportId != null;
        }
        return true;
    }
    
    // Validation method to ensure fare class configuration is valid
    @AssertTrue(message = "When fare class prices are provided, available seats per class must also be provided")
    private boolean isValidFareClassConfiguration() {
        if (fareClassPrices == null || fareClassPrices.isEmpty()) {
            return true; // No fare classes provided, so no validation needed
        }
        
        // If fare classes are provided, availableSeatsPerClass must have matching entries
        return availableSeatsPerClass != null && 
               availableSeatsPerClass.keySet().containsAll(fareClassPrices.keySet());
    }
    
    // Getters and Setters for new fields
    public FlightType getFlightType() {
        return flightType;
    }
    
    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }
    
    public Long getOriginLocationId() {
        return originLocationId;
    }
    
    public void setOriginLocationId(Long originLocationId) {
        this.originLocationId = originLocationId;
    }
    
    public Long getDestinationLocationId() {
        return destinationLocationId;
    }
    
    public void setDestinationLocationId(Long destinationLocationId) {
        this.destinationLocationId = destinationLocationId;
    }
    
    public Long getOriginAirportId() {
        return originAirportId;
    }
    
    public void setOriginAirportId(Long originAirportId) {
        this.originAirportId = originAirportId;
    }
    
    public Long getDestinationAirportId() {
        return destinationAirportId;
    }
    
    public void setDestinationAirportId(Long destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }

    // Getters and Setters
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Map<FareClass, Double> getFareClassPrices() {
        return fareClassPrices;
    }

    public void setFareClassPrices(Map<FareClass, Double> fareClassPrices) {
        this.fareClassPrices = fareClassPrices != null ? new EnumMap<>(fareClassPrices) : new EnumMap<>(FareClass.class);
    }

    public Map<FareClass, Integer> getAvailableSeatsPerClass() {
        return availableSeatsPerClass;
    }

    public void setAvailableSeatsPerClass(Map<FareClass, Integer> availableSeatsPerClass) {
        this.availableSeatsPerClass = availableSeatsPerClass != null ? new EnumMap<>(availableSeatsPerClass) : new EnumMap<>(FareClass.class);
    }
}
