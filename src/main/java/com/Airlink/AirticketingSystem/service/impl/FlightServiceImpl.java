package com.Airlink.AirticketingSystem.service.impl;

import com.Airlink.AirticketingSystem.dto.FlightRequestDTO;
import com.Airlink.AirticketingSystem.dto.FlightResponseDTO;
import com.Airlink.AirticketingSystem.dto.LocationResponseDTO;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.FareClassPrice;
import com.Airlink.AirticketingSystem.model.Airport;
import com.Airlink.AirticketingSystem.model.Flight;
import com.Airlink.AirticketingSystem.model.Location;
import com.Airlink.AirticketingSystem.model.enums.FareClass;
import com.Airlink.AirticketingSystem.model.enums.FlightStatus;
import com.Airlink.AirticketingSystem.model.enums.FlightType;
import com.Airlink.AirticketingSystem.repository.FlightRepository;
import com.Airlink.AirticketingSystem.repository.AirportRepository;
import com.Airlink.AirticketingSystem.repository.LocationRepository;
import com.Airlink.AirticketingSystem.service.FlightService;
import com.Airlink.AirticketingSystem.service.PricingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {
    
    private static final Logger logger = Logger.getLogger(FlightServiceImpl.class.getName());

    private final FlightRepository flightRepository;
    private final LocationRepository locationRepository;
    private final AirportRepository airportRepository;
    private final ModelMapper modelMapper;
    private final PricingService pricingService;

    public FlightServiceImpl(FlightRepository flightRepository,
                           LocationRepository locationRepository,
                           AirportRepository airportRepository,
                           ModelMapper modelMapper,
                           PricingService pricingService) {
        this.flightRepository = flightRepository;
        this.locationRepository = locationRepository;
        this.airportRepository = airportRepository;
        this.modelMapper = modelMapper;
        this.pricingService = pricingService;
    }

    @Override
    public FlightResponseDTO createFlight(FlightRequestDTO flightRequestDTO) {
        // Create new flight
        Flight flight = new Flight();
        flight.setFlightNumber(flightRequestDTO.getFlightNumber());
        flight.setAirline(flightRequestDTO.getAirline());
        flight.setFlightType(flightRequestDTO.getFlightType());
        flight.setDepartureTime(flightRequestDTO.getDepartureTime());
        flight.setArrivalTime(flightRequestDTO.getArrivalTime());
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setPrice(flightRequestDTO.getPrice());
        
        if (flightRequestDTO.getFlightType() == FlightType.DOMESTIC) {
            // Handle domestic flight - use Location for origin/destination
            Location origin = locationRepository.findById(flightRequestDTO.getOriginLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Origin location not found with id: " + flightRequestDTO.getOriginLocationId()));
            Location destination = locationRepository.findById(flightRequestDTO.getDestinationLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination location not found with id: " + flightRequestDTO.getDestinationLocationId()));
            
            flight.setOriginLocation(origin);
            flight.setDestinationLocation(destination);
            
            // Set airports to null for domestic flights
            flight.setOriginAirport(null);
            flight.setDestinationAirport(null);
        } else {
            // Handle international flight - use Airport for origin/destination
            Airport originAirport = airportRepository.findById(flightRequestDTO.getOriginAirportId())
                    .orElseThrow(() -> new ResourceNotFoundException("Origin airport not found with id: " + flightRequestDTO.getOriginAirportId()));
            Airport destinationAirport = airportRepository.findById(flightRequestDTO.getDestinationAirportId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found with id: " + flightRequestDTO.getDestinationAirportId()));
            
            flight.setOriginAirport(originAirport);
            flight.setDestinationAirport(destinationAirport);
            
            // Set locations to null for international flights
            flight.setOriginLocation(null);
            flight.setDestinationLocation(null);
        }
        
        // Set capacity and available seats
        if (flightRequestDTO.getTotalCapacity() != null) {
            flight.setTotalCapacity(flightRequestDTO.getTotalCapacity());
            flight.setAvailableSeats(flightRequestDTO.getAvailableSeats() != null ? 
                flightRequestDTO.getAvailableSeats() : flightRequestDTO.getTotalCapacity());
        }
        
        // Handle fare class prices if provided
        if (flightRequestDTO.getFareClassPrices() != null && !flightRequestDTO.getFareClassPrices().isEmpty()) {
            // Validate that available seats are provided for each fare class
            if (flightRequestDTO.getAvailableSeatsPerClass() == null || 
                !flightRequestDTO.getAvailableSeatsPerClass().keySet().containsAll(flightRequestDTO.getFareClassPrices().keySet())) {
                throw new IllegalArgumentException("Available seats must be provided for each fare class");
            }
            
            // Set fare class prices and available seats
            flightRequestDTO.getFareClassPrices().forEach((fareClass, price) -> {
                Integer seats = flightRequestDTO.getAvailableSeatsPerClass().get(fareClass);
                flight.addFareClassPrice(fareClass, price, seats);
            });
        }

        Flight savedFlight = flightRepository.save(flight);
        return convertToDTO(savedFlight);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponseDTO getFlightById(Long id) {
        Flight flight = flightRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        return convertToDTO(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponseDTO> getAllFlights(Pageable pageable) {
        // Get the page of flight IDs first
        Page<Long> flightIds = flightRepository.findAllIds(pageable);
        
        // Fetch all flights with their relationships using the IDs
        List<Flight> flights = flightRepository.findAllWithRelationships(flightIds.getContent());
        
        // Convert to DTOs
        List<FlightResponseDTO> dtos = flights.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
                
        return new PageImpl<>(dtos, pageable, flightIds.getTotalElements());
    }

    @Override
    public Page<FlightResponseDTO> searchFlights(String query, FlightType flightType, LocalDateTime departureDate, Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 10); // Default pagination
        }
        
        Page<Flight> flightsPage;
        
        try {
            if (query != null && !query.trim().isEmpty()) {
                // Search by query string
                if (flightType == null) {
                    flightsPage = flightRepository.searchFlights(query, pageable);
                } else if (flightType == FlightType.DOMESTIC) {
                    flightsPage = flightRepository.searchDomesticFlights(query, pageable);
                } else {
                    flightsPage = flightRepository.searchInternationalFlights(query, pageable);
                }
            } else {
                // Fallback to basic search if no query provided
                if (flightType != null) {
                    flightsPage = flightRepository.findByFlightType(flightType, pageable);
                } else {
                    flightsPage = flightRepository.findAll(pageable);
                }
            }
            
            // Apply date filter if provided
            if (departureDate != null) {
                LocalDateTime startOfDay = departureDate.toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1);
                
                List<Flight> filteredFlights = flightsPage.getContent().stream()
                    .filter(flight -> 
                        flight != null && 
                        flight.getDepartureTime() != null &&
                        !flight.getDepartureTime().isBefore(startOfDay) && 
                        flight.getDepartureTime().isBefore(endOfDay)
                    )
                    .collect(Collectors.toList());
                
                // Convert back to page
                return new PageImpl<>(
                    filteredFlights,
                    pageable,
                    filteredFlights.size()
                ).map(this::convertToDTO);
            }
            
            return flightsPage.map(this::convertToDTO);
            
        } catch (Exception e) {
            // Log the error and return empty page
            logger.severe("Error searching flights: " + e.getMessage());
            return new PageImpl<>(Collections.emptyList());
        }
    }

    @Override
    public FlightResponseDTO updateFlight(Long id, FlightRequestDTO flightRequestDTO) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        // Update basic flight information
        flight.setFlightNumber(flightRequestDTO.getFlightNumber());
        flight.setAirline(flightRequestDTO.getAirline());
        flight.setFlightType(flightRequestDTO.getFlightType());
        flight.setDepartureTime(flightRequestDTO.getDepartureTime());
        flight.setArrivalTime(flightRequestDTO.getArrivalTime());
        flight.setStatus(FlightStatus.valueOf(flightRequestDTO.getStatus()));
        flight.setTotalCapacity(flightRequestDTO.getTotalCapacity());
        flight.setAvailableSeats(flightRequestDTO.getAvailableSeats());

        // Update origin and destination based on flight type
        if (flightRequestDTO.getFlightType() == FlightType.DOMESTIC) {
            // Handle domestic flight - use Location for origin/destination
            if (flightRequestDTO.getOriginLocationId() != null && 
                (flight.getOriginLocation() == null || 
                 !flight.getOriginLocation().getId().equals(flightRequestDTO.getOriginLocationId()))) {
                Location origin = locationRepository.findById(flightRequestDTO.getOriginLocationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Origin location not found with id: " + flightRequestDTO.getOriginLocationId()));
                flight.setOriginLocation(origin);
                flight.setOriginAirport(null);
            }

            if (flightRequestDTO.getDestinationLocationId() != null && 
                (flight.getDestinationLocation() == null || 
                 !flight.getDestinationLocation().getId().equals(flightRequestDTO.getDestinationLocationId()))) {
                Location destination = locationRepository.findById(flightRequestDTO.getDestinationLocationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Destination location not found with id: " + flightRequestDTO.getDestinationLocationId()));
                flight.setDestinationLocation(destination);
                flight.setDestinationAirport(null);
            }
        } else {
            // Handle international flight - use Airport for origin/destination
            if (flightRequestDTO.getOriginAirportId() != null && 
                (flight.getOriginAirport() == null || 
                 !flight.getOriginAirport().getId().equals(flightRequestDTO.getOriginAirportId()))) {
                Airport originAirport = airportRepository.findById(flightRequestDTO.getOriginAirportId())
                        .orElseThrow(() -> new ResourceNotFoundException("Origin airport not found with id: " + flightRequestDTO.getOriginAirportId()));
                flight.setOriginAirport(originAirport);
                flight.setOriginLocation(null);
            }

            if (flightRequestDTO.getDestinationAirportId() != null && 
                (flight.getDestinationAirport() == null || 
                 !flight.getDestinationAirport().getId().equals(flightRequestDTO.getDestinationAirportId()))) {
                Airport destinationAirport = airportRepository.findById(flightRequestDTO.getDestinationAirportId())
                        .orElseThrow(() -> new ResourceNotFoundException("Destination airport not found with id: " + flightRequestDTO.getDestinationAirportId()));
                flight.setDestinationAirport(destinationAirport);
                flight.setDestinationLocation(null);
            }
        }

        // Update fare class prices if provided
        if (flightRequestDTO.getFareClassPrices() != null) {
            flight.getFareClassPrices().clear();
            
            if (!flightRequestDTO.getFareClassPrices().isEmpty()) {
                // Validate that available seats are provided for each fare class
                if (flightRequestDTO.getAvailableSeatsPerClass() == null || 
                    !flightRequestDTO.getAvailableSeatsPerClass().keySet().containsAll(flightRequestDTO.getFareClassPrices().keySet())) {
                    throw new IllegalArgumentException("Available seats must be provided for each fare class");
                }
                
                // Add all fare class prices
                flightRequestDTO.getFareClassPrices().forEach((fareClass, price) -> {
                    Integer seats = flightRequestDTO.getAvailableSeatsPerClass().get(fareClass);
                    flight.addFareClassPrice(fareClass, price, seats);
                });
                
                // Set the base price to economy class price or first available price
                double basePrice = flightRequestDTO.getFareClassPrices().getOrDefault(
                    FareClass.ECONOMY, 
                    flightRequestDTO.getFareClassPrices().values().iterator().next()
                );
                flight.setPrice(basePrice);
            }
        }

        Flight updatedFlight = flightRepository.save(flight);
        return convertToDTO(updatedFlight);
    }

    @Override
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        flightRepository.delete(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponseDTO> getFlightsByStatus(String status, Pageable pageable) {
        FlightStatus flightStatus = FlightStatus.valueOf(status);
        return flightRepository.findByStatus(flightStatus, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponseDTO> getFlightsByOrigin(Long originId, FlightType flightType, Pageable pageable) {
        if (flightType == FlightType.DOMESTIC) {
            return flightRepository.findByOriginLocationId(originId, pageable)
                    .map(this::convertToDTO);
        } else {
            return flightRepository.findByOriginAirportId(originId, pageable)
                    .map(this::convertToDTO);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponseDTO> getFlightsByDestination(Long destinationId, FlightType flightType, Pageable pageable) {
        if (flightType == FlightType.DOMESTIC) {
            return flightRepository.findByDestinationLocationId(destinationId, pageable)
                    .map(this::convertToDTO);
        } else {
            return flightRepository.findByDestinationAirportId(destinationId, pageable)
                    .map(this::convertToDTO);
        }
    }

    private FlightResponseDTO convertToDTO(Flight flight) {
        FlightResponseDTO dto = modelMapper.map(flight, FlightResponseDTO.class);
        dto.setFlightType(flight.getFlightType());
        
        // Map origin and destination details based on flight type
        if (flight.getFlightType() == FlightType.DOMESTIC) {
            if (flight.getOriginLocation() != null) {
                dto.setOriginLocationId(flight.getOriginLocation().getId());
                dto.setOriginLocationName(flight.getOriginLocation().getName());
                dto.setOriginLocationCode(flight.getOriginLocation().getCode());
            }
            if (flight.getDestinationLocation() != null) {
                dto.setDestinationLocationId(flight.getDestinationLocation().getId());
                dto.setDestinationLocationName(flight.getDestinationLocation().getName());
                dto.setDestinationLocationCode(flight.getDestinationLocation().getCode());
            }
        } else {
            if (flight.getOriginAirport() != null) {
                dto.setOriginAirportId(flight.getOriginAirport().getId());
                dto.setOriginAirportName(flight.getOriginAirport().getName());
                dto.setOriginAirportCode(flight.getOriginAirport().getCode());
                dto.setOriginAirportCity(flight.getOriginAirport().getCity());
                dto.setOriginAirportCountry(flight.getOriginAirport().getCountry());
            }
            if (flight.getDestinationAirport() != null) {
                dto.setDestinationAirportId(flight.getDestinationAirport().getId());
                dto.setDestinationAirportName(flight.getDestinationAirport().getName());
                dto.setDestinationAirportCode(flight.getDestinationAirport().getCode());
                dto.setDestinationAirportCity(flight.getDestinationAirport().getCity());
                dto.setDestinationAirportCountry(flight.getDestinationAirport().getCountry());
            }
        }
        
        // Map fare class prices if any
        if (flight.getFareClassPrices() != null && !flight.getFareClassPrices().isEmpty()) {
            Map<FareClass, Double> fareClassPrices = new EnumMap<>(FareClass.class);
            flight.getFareClassPrices().forEach(fcp -> 
                fareClassPrices.put(fcp.getFareClass(), fcp.getBasePrice())
            );
            dto.setFareClassPrices(fareClassPrices);
            // Set the base price to economy class price or first available price
            if (!fareClassPrices.isEmpty()) {
                dto.setPrice(fareClassPrices.getOrDefault(FareClass.ECONOMY, 
                    fareClassPrices.values().iterator().next()));
            }
        } else {
            dto.setPrice(flight.getPrice());
        }

        return dto;
    }

    @Deprecated
    private LocationResponseDTO convertToLocationDTO(Location location) {
        if (location == null) return null;
        LocationResponseDTO dto = modelMapper.map(location, LocationResponseDTO.class);
        if (location.getParent() != null) {
            dto.setParentId(location.getParent().getId());
            dto.setParentName(location.getParent().getName());
        }
        return dto;
    }
}

