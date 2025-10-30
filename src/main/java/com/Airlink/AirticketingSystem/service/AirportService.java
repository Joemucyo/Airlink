package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.AirportRequestDTO;
import com.Airlink.AirticketingSystem.dto.AirportResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.AirportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AirportService {
    AirportResponseDTO createAirport(AirportRequestDTO request);
    AirportResponseDTO getAirportById(Long id);
    AirportResponseDTO getAirportByCode(String code);
    Page<AirportResponseDTO> getAllAirports(Pageable pageable);
    Page<AirportResponseDTO> getAirportsByType(AirportType type, Pageable pageable);
    Page<AirportResponseDTO> searchAirports(String query, Pageable pageable);
    AirportResponseDTO updateAirport(Long id, AirportRequestDTO request);
    void deleteAirport(Long id);
    
    // New methods for location-based searches
    Page<AirportResponseDTO> getAirportsByCity(String city, Pageable pageable);
    Page<AirportResponseDTO> getAirportsByCountry(String country, Pageable pageable);
    List<AirportResponseDTO> getNearestAirports(Double latitude, Double longitude, Double radiusKm);
}

