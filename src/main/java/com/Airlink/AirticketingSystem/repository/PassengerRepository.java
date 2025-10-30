package com.Airlink.AirticketingSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Airlink.AirticketingSystem.model.Passenger;
import com.Airlink.AirticketingSystem.model.enums.Gender;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByPassportNumber(String passportNumber);
    List<Passenger> findByBooking_Id(Long bookingId);
    Page<Passenger> findAllByGender(Gender gender, Pageable pageable);
    Page<Passenger> findByBooking_Id(Long bookingId, Pageable pageable);
    boolean existsByPassportNumber(String passportNumber);
}
