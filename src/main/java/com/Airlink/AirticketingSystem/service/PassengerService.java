package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.PassengerResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PassengerService {
    PassengerResponseDTO createPassenger(Long bookingId, String firstName, String lastName, 
                                        String passportNumber, Gender gender, String dateOfBirth);
    PassengerResponseDTO getPassengerById(Long id);
    PassengerResponseDTO getPassengerByPassportNumber(String passportNumber);
    Page<PassengerResponseDTO> getAllPassengers(Pageable pageable);
    Page<PassengerResponseDTO> getPassengersByBooking(Long bookingId, Pageable pageable);
    Page<PassengerResponseDTO> getPassengersByGender(Gender gender, Pageable pageable);
    PassengerResponseDTO updatePassenger(Long id, String firstName, String lastName, String passportNumber);
    void deletePassenger(Long id);
}

