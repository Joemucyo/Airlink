package com.Airlink.AirticketingSystem.controller;

import com.Airlink.AirticketingSystem.dto.AirportRequestDTO;
import com.Airlink.AirticketingSystem.dto.AirportResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.AirportType;
import com.Airlink.AirticketingSystem.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @PostMapping
    public ResponseEntity<AirportResponseDTO> createAirport(@Valid @RequestBody AirportRequestDTO request) {
        AirportResponseDTO airport = airportService.createAirport(request);
        return new ResponseEntity<>(airport, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportResponseDTO> getAirportById(@PathVariable Long id) {
        AirportResponseDTO airport = airportService.getAirportById(id);
        return ResponseEntity.ok(airport);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<AirportResponseDTO> getAirportByCode(@PathVariable String code) {
        AirportResponseDTO airport = airportService.getAirportByCode(code);
        return ResponseEntity.ok(airport);
    }

    @GetMapping
    public ResponseEntity<Page<AirportResponseDTO>> getAllAirports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AirportResponseDTO> airports = airportService.getAllAirports(pageable);
        return ResponseEntity.ok(airports);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Page<AirportResponseDTO>> getAirportsByType(
            @PathVariable AirportType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AirportResponseDTO> airports = airportService.getAirportsByType(type, pageable);
        return ResponseEntity.ok(airports);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Page<AirportResponseDTO>> getAirportsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AirportResponseDTO> airports = airportService.getAirportsByCity(city, pageable);
        return ResponseEntity.ok(airports);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<Page<AirportResponseDTO>> getAirportsByCountry(
            @PathVariable String country,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AirportResponseDTO> airports = airportService.getAirportsByCountry(country, pageable);
        return ResponseEntity.ok(airports);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AirportResponseDTO>> searchAirports(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AirportResponseDTO> airports = airportService.searchAirports(query, pageable);
        return ResponseEntity.ok(airports);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<AirportResponseDTO>> getNearestAirports(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "100") double radiusKm) {
        List<AirportResponseDTO> airports = airportService.getNearestAirports(latitude, longitude, radiusKm);
        return ResponseEntity.ok(airports);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportResponseDTO> updateAirport(
            @PathVariable Long id,
            @Valid @RequestBody AirportRequestDTO request) {
        AirportResponseDTO airport = airportService.updateAirport(id, request);
        return ResponseEntity.ok(airport);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }
}

