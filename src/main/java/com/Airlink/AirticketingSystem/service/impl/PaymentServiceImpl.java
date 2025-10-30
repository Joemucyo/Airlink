package com.Airlink.AirticketingSystem.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Airlink.AirticketingSystem.dto.PaymentResponseDTO;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.Booking;
import com.Airlink.AirticketingSystem.model.Payment;
import com.Airlink.AirticketingSystem.model.enums.BookingStatus;
import com.Airlink.AirticketingSystem.model.enums.PaymentMethod;
import com.Airlink.AirticketingSystem.model.enums.PaymentStatus;
import com.Airlink.AirticketingSystem.repository.BookingRepository;
import com.Airlink.AirticketingSystem.repository.PaymentRepository;
import com.Airlink.AirticketingSystem.service.PaymentService;
import org.springframework.data.domain.PageImpl;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(Long bookingId, Double amount, PaymentMethod method, PaymentStatus status) {
        // Get the booking with its relationships
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        // Create the payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setPaymentReference(generateUniquePaymentReference());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(status);

        // Save the payment
        Payment savedPayment = paymentRepository.save(payment);

        // Update the booking's payment reference
        booking.setPayment(savedPayment);
        
        // Update booking status if payment is completed
        if (status == PaymentStatus.COMPLETED) {
            booking.setStatus(BookingStatus.CONFIRMED);
            
            // Ensure the booking's payment reference is set
            if (booking.getPayment() == null) {
                booking.setPayment(savedPayment);
            }
            
            // Save the booking with the updated status
            bookingRepository.saveAndFlush(booking);
        } else {
            // Save the booking with the payment reference
            bookingRepository.save(booking);
        }

        return convertToDTO(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        return convertToDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByReference(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with reference: " + reference));
        return convertToDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findAllByStatus(status, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getPaymentsByBooking(Long bookingId, Pageable pageable) {
    return paymentRepository.findAllByBooking_Id(bookingId, pageable)
        .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatus newStatus) {
        // Get the payment with its booking using a fresh database query
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        // Get the current status before updating
        PaymentStatus oldStatus = payment.getStatus();
        
        // If status is not changing, just return the current payment
        if (newStatus == oldStatus) {
            return convertToDTO(payment);
        }
        
        // Update the status
        payment.setStatus(newStatus);
        
        // If the payment is being marked as COMPLETED
        if (newStatus == PaymentStatus.COMPLETED) {
            // Get the booking with its relationships
            Booking booking = bookingRepository.findById(payment.getBooking().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking", payment.getBooking().getId()));
            
            // Update the booking status
            booking.setStatus(BookingStatus.CONFIRMED);
            
            // Ensure the booking's payment reference is set
            if (booking.getPayment() == null) {
                booking.setPayment(payment);
            }
            
            // Save the booking with updated status
            bookingRepository.saveAndFlush(booking);
        }
        
        // Save the payment
        Payment updatedPayment = paymentRepository.saveAndFlush(payment);
        
        return convertToDTO(updatedPayment);
    }

    @Override
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        paymentRepository.delete(payment);
    }

    private String generateUniquePaymentReference() {
        String reference;
        do {
            reference = "PAY-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        } while (paymentRepository.existsByPaymentReference(reference));
        return reference;
    }

    private PaymentResponseDTO convertToDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getMethod() != null ? payment.getMethod().toString() : null);
        dto.setPaymentStatus(payment.getStatus() != null ? payment.getStatus().toString() : null);
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}

