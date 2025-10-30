package com.Airlink.AirticketingSystem.controller;

import com.Airlink.AirticketingSystem.dto.PassengerRequestDTO;
import com.Airlink.AirticketingSystem.dto.PassengerResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.Gender;
import com.Airlink.AirticketingSystem.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    @Autowired
    private PassengerService passengerService;

    @PostMapping
    public ResponseEntity<PassengerResponseDTO> createPassenger(@Valid @RequestBody PassengerRequestDTO request) {
        PassengerResponseDTO passenger = passengerService.createPassenger(
            request.getBookingId(), 
            request.getFirstName(), 
            request.getLastName(), 
            request.getPassportNumber(), 
            request.getGender(), 
            request.getDateOfBirth().toString()
        );
        return new ResponseEntity<>(passenger, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> getPassengerById(@PathVariable Long id) {
        PassengerResponseDTO passenger = passengerService.getPassengerById(id);
        return ResponseEntity.ok(passenger);
    }

    @GetMapping("/passport/{passportNumber}")
    public ResponseEntity<PassengerResponseDTO> getPassengerByPassportNumber(@PathVariable String passportNumber) {
        PassengerResponseDTO passenger = passengerService.getPassengerByPassportNumber(passportNumber);
        return ResponseEntity.ok(passenger);
    }

    @GetMapping
    public ResponseEntity<Page<PassengerResponseDTO>> getAllPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PassengerResponseDTO> passengers = passengerService.getAllPassengers(pageable);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Page<PassengerResponseDTO>> getPassengersByBooking(
            @PathVariable Long bookingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PassengerResponseDTO> passengers = passengerService.getPassengersByBooking(bookingId, pageable);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/gender/{gender}")
    public ResponseEntity<Page<PassengerResponseDTO>> getPassengersByGender(
            @PathVariable Gender gender,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PassengerResponseDTO> passengers = passengerService.getPassengersByGender(gender, pageable);
        return ResponseEntity.ok(passengers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerResponseDTO> updatePassenger(
            @PathVariable Long id,
            @Valid @RequestBody PassengerRequestDTO request) {
        PassengerResponseDTO passenger = passengerService.updatePassenger(
            id, 
            request.getFirstName(), 
            request.getLastName(), 
            request.getPassportNumber()
        );
        return ResponseEntity.ok(passenger);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
}

