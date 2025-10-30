package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.LocationResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {
    LocationResponseDTO createLocation(String name, String code, LocationType type, Long parentId);
    LocationResponseDTO getLocationById(Long id);
    Page<LocationResponseDTO> getAllLocations(Pageable pageable);
    List<LocationResponseDTO> getLocationsByType(LocationType type);
    List<LocationResponseDTO> getChildrenLocations(Long parentId);
    LocationResponseDTO updateLocation(Long id, String name, String code, LocationType type, Long parentId);
    void deleteLocation(Long id);
    LocationResponseDTO getLocationHierarchy(Long id);
    List<LocationResponseDTO> searchLocationsByName(String name);
}

