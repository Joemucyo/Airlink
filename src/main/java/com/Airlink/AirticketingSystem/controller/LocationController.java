package com.Airlink.AirticketingSystem.controller;

import com.Airlink.AirticketingSystem.dto.LocationRequestDTO;
import com.Airlink.AirticketingSystem.dto.LocationResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.LocationType;
import com.Airlink.AirticketingSystem.service.LocationService;
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
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponseDTO> createLocation(@Valid @RequestBody LocationRequestDTO request) {
        LocationResponseDTO location = locationService.createLocation(
            request.getName(), 
            request.getCode(), 
            request.getType(), 
            request.getParentId()
        );
        return new ResponseEntity<>(location, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> getLocationById(@PathVariable Long id) {
        LocationResponseDTO location = locationService.getLocationById(id);
        return ResponseEntity.ok(location);
    }

    @GetMapping
    public ResponseEntity<Page<LocationResponseDTO>> getAllLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LocationResponseDTO> locations = locationService.getAllLocations(pageable);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<LocationResponseDTO>> getLocationsByType(@PathVariable LocationType type) {
        List<LocationResponseDTO> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationResponseDTO>> searchLocations(@RequestParam String name) {
        List<LocationResponseDTO> locations = locationService.searchLocationsByName(name);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<LocationResponseDTO>> getChildrenLocations(@PathVariable Long parentId) {
        List<LocationResponseDTO> locations = locationService.getChildrenLocations(parentId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}/hierarchy")
    public ResponseEntity<LocationResponseDTO> getLocationHierarchy(@PathVariable Long id) {
        LocationResponseDTO location = locationService.getLocationHierarchy(id);
        return ResponseEntity.ok(location);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponseDTO> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequestDTO request) {
        LocationResponseDTO location = locationService.updateLocation(
            id, 
            request.getName(), 
            request.getCode(), 
            request.getType(), 
            request.getParentId()
        );
        return ResponseEntity.ok(location);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}

