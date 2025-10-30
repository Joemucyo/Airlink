package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.FlightRequestDTO;
import com.Airlink.AirticketingSystem.dto.FlightResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.FlightType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface FlightService {
    FlightResponseDTO createFlight(FlightRequestDTO request);
    FlightResponseDTO getFlightById(Long id);
    Page<FlightResponseDTO> getAllFlights(Pageable pageable);
    // Search flights by query string and flight type
    Page<FlightResponseDTO> searchFlights(String query, FlightType flightType, LocalDateTime departureDate, Pageable pageable);
    
    // For backward compatibility
    @Deprecated
    default Page<FlightResponseDTO> searchFlights(Long originId, Long destinationId, LocalDateTime departureDate, Pageable pageable) {
        throw new UnsupportedOperationException("This method is deprecated. Use searchFlights(String, FlightType, LocalDateTime, Pageable) instead.");
    }
    
    // For backward compatibility
    @Deprecated
    default Page<FlightResponseDTO> searchFlights(Long originId, Long destinationId, LocalDateTime departureDate, Integer passengerCount, Pageable pageable) {
        throw new UnsupportedOperationException("This method is deprecated. Use searchFlights(String, FlightType, LocalDateTime, Pageable) instead.");
    }
    FlightResponseDTO updateFlight(Long id, FlightRequestDTO request);
    void deleteFlight(Long id);
    Page<FlightResponseDTO> getFlightsByStatus(String status, Pageable pageable);
    // Get flights by origin with flight type
    Page<FlightResponseDTO> getFlightsByOrigin(Long originId, FlightType flightType, Pageable pageable);
    
    // Get flights by destination with flight type
    Page<FlightResponseDTO> getFlightsByDestination(Long destinationId, FlightType flightType, Pageable pageable);
    
    // For backward compatibility
    @Deprecated
    default Page<FlightResponseDTO> getFlightsByOrigin(Long originId, Pageable pageable) {
        return getFlightsByOrigin(originId, null, pageable);
    }
    
    // For backward compatibility
    @Deprecated
    default Page<FlightResponseDTO> getFlightsByDestination(Long destinationId, Pageable pageable) {
        return getFlightsByDestination(destinationId, null, pageable);
    }
}

