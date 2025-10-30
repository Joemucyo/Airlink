package com.Airlink.AirticketingSystem.service.impl;

import com.Airlink.AirticketingSystem.dto.AirportRequestDTO;
import com.Airlink.AirticketingSystem.dto.AirportResponseDTO;
import com.Airlink.AirticketingSystem.exception.BadRequestException;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.Airport;
import com.Airlink.AirticketingSystem.model.enums.AirportType;
import com.Airlink.AirticketingSystem.repository.AirportRepository;
import com.Airlink.AirticketingSystem.service.AirportService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AirportServiceImpl implements AirportService {

    private final AirportRepository airportRepository;
    private final ModelMapper modelMapper;

    public AirportServiceImpl(AirportRepository airportRepository, ModelMapper modelMapper) {
        this.airportRepository = airportRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AirportResponseDTO createAirport(AirportRequestDTO request) {
        // Check if code already exists
        if (airportRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Airport code already exists: " + request.getCode());
        }

        // Check if airport with same name and city exists
        if (airportRepository.existsByNameAndCityIgnoreCase(request.getName(), request.getCity())) {
            throw new BadRequestException("Airport with this name already exists in " + request.getCity());
        }

        Airport airport = new Airport();
        airport.setName(request.getName());
        airport.setCode(request.getCode());
        airport.setDescription(request.getDescription());
        airport.setType(request.getType());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());
        airport.setIataCityCode(request.getIataCityCode());
        airport.setLatitude(request.getLatitude());
        airport.setLongitude(request.getLongitude());
        airport.setTimezone(request.getTimezone());

        Airport savedAirport = airportRepository.save(airport);
        return convertToDTO(savedAirport);
    }

    @Override
    @Transactional(readOnly = true)
    public AirportResponseDTO getAirportById(Long id) {
        return airportRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Airport", id));
    }

    @Override
    @Transactional(readOnly = true)
    public AirportResponseDTO getAirportByCode(String code) {
        return airportRepository.findByCode(code)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Airport with code: " + code));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirportResponseDTO> getAllAirports(Pageable pageable) {
        return airportRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirportResponseDTO> getAirportsByType(AirportType type, Pageable pageable) {
        return airportRepository.findByType(type, pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirportResponseDTO> getAirportsByCity(String city, Pageable pageable) {
        return airportRepository.findByCityIgnoreCase(city, pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirportResponseDTO> getAirportsByCountry(String country, Pageable pageable) {
        return airportRepository.findByCountryIgnoreCase(country, pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirportResponseDTO> searchAirports(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return airportRepository.findAll(pageable).map(this::convertToDTO);
        }
        return airportRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                query, query, pageable).map(this::convertToDTO);
    }

    @Override
    public AirportResponseDTO updateAirport(Long id, AirportRequestDTO request) {
        Airport airport = airportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airport", id));

        if (request.getName() != null) {
            airport.setName(request.getName());
        }
        if (request.getCode() != null && !request.getCode().equals(airport.getCode())) {
            if (airportRepository.existsByCode(request.getCode())) {
                throw new BadRequestException("Airport code already exists");
            }
            airport.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            airport.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            airport.setType(request.getType());
        }
        if (request.getCity() != null) {
            airport.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            airport.setCountry(request.getCountry());
        }
        if (request.getIataCityCode() != null) {
            airport.setIataCityCode(request.getIataCityCode());
        }
        if (request.getLatitude() != null) {
            airport.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            airport.setLongitude(request.getLongitude());
        }
        if (request.getTimezone() != null) {
            airport.setTimezone(request.getTimezone());
        }

        Airport updatedAirport = airportRepository.save(airport);
        return convertToDTO(updatedAirport);
    }

    @Override
    public void deleteAirport(Long id) {
        if (!airportRepository.existsById(id)) {
            throw new ResourceNotFoundException("Airport", id);
        }
        airportRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AirportResponseDTO> getNearestAirports(Double latitude, Double longitude, Double radiusKm) {
        if (latitude == null || longitude == null) {
            throw new BadRequestException("Latitude and longitude are required");
        }
        if (radiusKm == null || radiusKm <= 0) {
            radiusKm = 100.0; // Default radius of 100km
        }
        return airportRepository.findNearbyAirports(latitude, longitude, radiusKm).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private AirportResponseDTO convertToDTO(Airport airport) {
        if (airport == null) {
            return null;
        }

        AirportResponseDTO dto = modelMapper.map(airport, AirportResponseDTO.class);
        
        // Flight counts: the domain currently models flights via a
        // many-to-many join (Flight.airports). For now we do not compute
        // departing/arriving counts here to avoid expensive queries; set
        // them to zero. If you want accurate counts add repository methods
        // to count flights for an airport or add explicit departure/arrival
        // fields on Flight.
        dto.setDepartingFlightsCount(0);
        dto.setArrivingFlightsCount(0);
        
        return dto;
    }
}

