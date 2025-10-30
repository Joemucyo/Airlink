package com.Airlink.AirticketingSystem.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BookingRequestDTO {
    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

    private List<PassengerRequestDTO> passengers;

    // Getters and Setters
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<PassengerRequestDTO> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerRequestDTO> passengers) { this.passengers = passengers; }
}

