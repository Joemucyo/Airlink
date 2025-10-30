package com.Airlink.AirticketingSystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookingResponseDTO {
    private Long id;
    private String bookingCode;
    private LocalDateTime bookingDate;
    private FlightResponseDTO flight;
    private UserResponseDTO user;
    private Double totalAmount;
    private String status;
    private List<PassengerResponseDTO> passengers;
    private PaymentResponseDTO payment;

    public BookingResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public FlightResponseDTO getFlight() { return flight; }
    public void setFlight(FlightResponseDTO flight) { this.flight = flight; }

    public UserResponseDTO getUser() { return user; }
    public void setUser(UserResponseDTO user) { this.user = user; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<PassengerResponseDTO> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerResponseDTO> passengers) { this.passengers = passengers; }

    public PaymentResponseDTO getPayment() { return payment; }
    public void setPayment(PaymentResponseDTO payment) { this.payment = payment; }
}

