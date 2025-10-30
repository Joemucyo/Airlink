package com.Airlink.AirticketingSystem.controller;

import com.Airlink.AirticketingSystem.dto.FlightRequestDTO;
import com.Airlink.AirticketingSystem.dto.FlightResponseDTO;
import com.Airlink.AirticketingSystem.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping
    public ResponseEntity<FlightResponseDTO> createFlight(@Valid @RequestBody FlightRequestDTO request) {
        FlightResponseDTO createdFlight = flightService.createFlight(request);
        return new ResponseEntity<>(createdFlight, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id) {
        FlightResponseDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @GetMapping
    public ResponseEntity<Page<FlightResponseDTO>> getAllFlights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<FlightResponseDTO> flights = flightService.getAllFlights(pageable);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FlightResponseDTO>> searchFlights(
            @RequestParam(required = false) Long originId,
            @RequestParam(required = false) Long destinationId,
            @RequestParam(required = false) String departureDate,
            @RequestParam(required = false, defaultValue = "1") int passengerCount,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        LocalDateTime departureDateTime = null;
        if (departureDate != null && !departureDate.isEmpty()) {
            departureDateTime = LocalDateTime.parse(departureDate);
        }
        
        // Ensure passenger count is at least 1
        passengerCount = Math.max(1, passengerCount);
        
        Page<FlightResponseDTO> flights = flightService.searchFlights(
            originId, 
            destinationId, 
            departureDateTime, 
            passengerCount,
            pageable
        );
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<FlightResponseDTO>> getFlightsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FlightResponseDTO> flights = flightService.getFlightsByStatus(status, pageable);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/origin/{originId}")
    public ResponseEntity<Page<FlightResponseDTO>> getFlightsByOrigin(
            @PathVariable Long originId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FlightResponseDTO> flights = flightService.getFlightsByOrigin(originId, pageable);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<Page<FlightResponseDTO>> getFlightsByDestination(
            @PathVariable Long destinationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FlightResponseDTO> flights = flightService.getFlightsByDestination(destinationId, pageable);
        return ResponseEntity.ok(flights);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequestDTO request) {
        FlightResponseDTO updatedFlight = flightService.updateFlight(id, request);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}

