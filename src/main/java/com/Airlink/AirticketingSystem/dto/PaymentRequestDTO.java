package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.PaymentMethod;
import com.Airlink.AirticketingSystem.model.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequestDTO {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
    
    private PaymentStatus status = PaymentStatus.PENDING;
    
    // Default constructor
    public PaymentRequestDTO() {}
    
    // Constructor with all fields
    public PaymentRequestDTO(Long bookingId, Double amount, PaymentMethod method, PaymentStatus status) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
