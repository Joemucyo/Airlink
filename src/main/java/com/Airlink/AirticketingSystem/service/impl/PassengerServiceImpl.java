package com.Airlink.AirticketingSystem.service.impl;

import java.time.LocalDate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Airlink.AirticketingSystem.dto.PassengerResponseDTO;
import com.Airlink.AirticketingSystem.exception.BadRequestException;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.Booking;
import com.Airlink.AirticketingSystem.model.Passenger;
import com.Airlink.AirticketingSystem.model.enums.Gender;
import com.Airlink.AirticketingSystem.repository.BookingRepository;
import com.Airlink.AirticketingSystem.repository.PassengerRepository;
import com.Airlink.AirticketingSystem.service.PassengerService;

@Service
@Transactional
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PassengerResponseDTO createPassenger(Long bookingId, String firstName, String lastName,
                                                String passportNumber, Gender gender, String dateOfBirthStr) {
        // Validate passport uniqueness
        if (passengerRepository.existsByPassportNumber(passportNumber)) {
            throw new BadRequestException("Passport number already exists: " + passportNumber);
        }

        // Validate booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));

        Passenger passenger = new Passenger();
        passenger.setFirstName(firstName);
        passenger.setLastName(lastName);
        passenger.setPassportNumber(passportNumber);
        passenger.setGender(gender);
        passenger.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
        passenger.setBooking(booking);

        Passenger savedPassenger = passengerRepository.save(passenger);
        return convertToDTO(savedPassenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDTO getPassengerById(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", id));
        return convertToDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public PassengerResponseDTO getPassengerByPassportNumber(String passportNumber) {
        Passenger passenger = passengerRepository.findByPassportNumber(passportNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger with passport: " + passportNumber));
        return convertToDTO(passenger);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassengerResponseDTO> getAllPassengers(Pageable pageable) {
        return passengerRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassengerResponseDTO> getPassengersByBooking(Long bookingId, Pageable pageable) {
        return passengerRepository.findByBooking_Id(bookingId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PassengerResponseDTO> getPassengersByGender(Gender gender, Pageable pageable) {
        return passengerRepository.findAllByGender(gender, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public PassengerResponseDTO updatePassenger(Long id, String firstName, String lastName, String passportNumber) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", id));

        if (firstName != null) passenger.setFirstName(firstName);
        if (lastName != null) passenger.setLastName(lastName);
        if (passportNumber != null) {
            // Check uniqueness if changing passport
            if (!passportNumber.equals(passenger.getPassportNumber()) && 
                passengerRepository.existsByPassportNumber(passportNumber)) {
                throw new BadRequestException("Passport number already exists");
            }
            passenger.setPassportNumber(passportNumber);
        }

        Passenger updatedPassenger = passengerRepository.save(passenger);
        return convertToDTO(updatedPassenger);
    }

    @Override
    public void deletePassenger(Long id) {
        Passenger passenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", id));
        passengerRepository.delete(passenger);
    }

    private PassengerResponseDTO convertToDTO(Passenger passenger) {
        PassengerResponseDTO dto = modelMapper.map(passenger, PassengerResponseDTO.class);
        return dto;
    }
}

