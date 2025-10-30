package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.PaymentResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.PaymentMethod;
import com.Airlink.AirticketingSystem.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDTO createPayment(Long bookingId, Double amount, PaymentMethod method, PaymentStatus status);
    PaymentResponseDTO getPaymentById(Long id);
    PaymentResponseDTO getPaymentByReference(String reference);
    Page<PaymentResponseDTO> getAllPayments(Pageable pageable);
    Page<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable);
    Page<PaymentResponseDTO> getPaymentsByBooking(Long bookingId, Pageable pageable);
    PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatus status);
    void deletePayment(Long id);
}

