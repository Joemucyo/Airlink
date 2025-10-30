package com.Airlink.AirticketingSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Airlink.AirticketingSystem.model.Payment;
import com.Airlink.AirticketingSystem.model.Booking;
import com.Airlink.AirticketingSystem.model.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBooking(Booking booking);
    Page<Payment> findAllByStatus(PaymentStatus status, Pageable pageable);
    Page<Payment> findAllByBooking_Id(Long bookingId, Pageable pageable);
    boolean existsByPaymentReference(String paymentReference);
    Optional<Payment> findByPaymentReference(String paymentReference);
    
    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.booking WHERE p.id = :id")
    Optional<Payment> findByIdWithBooking(@Param("id") Long id);
    
    @Override
    @org.springframework.transaction.annotation.Transactional
    <S extends Payment> S saveAndFlush(S entity);
    
    @org.springframework.transaction.annotation.Transactional
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Payment refresh(@Param("id") Long id);
    
    default void refresh(Payment payment) {
        if (payment != null && payment.getId() != null) {
            Payment refreshed = refresh(payment.getId());
            if (refreshed != null) {
                payment.setAmount(refreshed.getAmount());
                payment.setStatus(refreshed.getStatus());
                payment.setMethod(refreshed.getMethod());
                payment.setPaymentReference(refreshed.getPaymentReference());
                payment.setPaymentDate(refreshed.getPaymentDate());
                // Don't update the booking reference to avoid potential issues
            }
        }
    }
}
