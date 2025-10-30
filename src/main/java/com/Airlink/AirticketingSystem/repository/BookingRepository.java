package com.Airlink.AirticketingSystem.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Airlink.AirticketingSystem.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingCode(String bookingCode);
    Page<Booking> findByUser_Id(Long userId, Pageable pageable);
    Page<Booking> findByFlight_Id(Long flightId, Pageable pageable);
    boolean existsByBookingCode(String bookingCode);
    Page<Booking> findAllByBookingDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @EntityGraph(attributePaths = {"payment", "flight", "user", "passengers"})
    Optional<Booking> findWithPaymentById(Long id);
    
    @EntityGraph(attributePaths = {"payment", "flight", "user", "passengers"})
    Optional<Booking> findWithPaymentByBookingCode(String bookingCode);
}
