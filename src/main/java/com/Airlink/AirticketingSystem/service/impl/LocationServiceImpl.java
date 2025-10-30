package com.Airlink.AirticketingSystem.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Airlink.AirticketingSystem.dto.LocationResponseDTO;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.Location;
import com.Airlink.AirticketingSystem.model.enums.LocationType;
import com.Airlink.AirticketingSystem.repository.LocationRepository;
import com.Airlink.AirticketingSystem.service.LocationService;

@Service
@Transactional
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LocationResponseDTO createLocation(String name, String code, LocationType type, Long parentId) {
        Location location = new Location();
        location.setName(name);
        
        // Auto-generate code if not provided
        if (code == null || code.trim().isEmpty()) {
            code = generateLocationCode(type, parentId);
        }
        location.setCode(code);
        location.setType(type);

        if (parentId != null) {
            Location parent = locationRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location", parentId));
            location.setParent(parent);
            // Copy parent's hierarchy fields
            copyParentHierarchy(location, parent);
        }
        
        // Set the appropriate field based on type
        setLocationHierarchyField(location);

        Location savedLocation = locationRepository.save(location);
        return convertToDTO(savedLocation);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponseDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));
        return convertToDTO(location);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LocationResponseDTO> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getLocationsByType(LocationType type) {
        List<Location> locations = locationRepository.findByType(type);
        return locations.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getChildrenLocations(Long parentId) {
        List<Location> children = locationRepository.findByParent_Id(parentId);
        return children.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public LocationResponseDTO updateLocation(Long id, String name, String code, LocationType type, Long parentId) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        boolean needsHierarchyUpdate = false;
        
        if (name != null) location.setName(name);
        if (code != null) location.setCode(code);
        
        if (type != null && !type.equals(location.getType())) {
            location.setType(type);
            needsHierarchyUpdate = true;
        }

        if (parentId != null && (location.getParent() == null || !parentId.equals(location.getParent().getId()))) {
            Location parent = locationRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location", parentId));
            location.setParent(parent);
            copyParentHierarchy(location, parent);
            needsHierarchyUpdate = true;
        }
        
        if (needsHierarchyUpdate) {
            setLocationHierarchyField(location);
        }

        Location updatedLocation = locationRepository.save(location);
        return convertToDTO(updatedLocation);
    }

    @Override
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));
        locationRepository.delete(location);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponseDTO getLocationHierarchy(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", id));

        LocationResponseDTO dto = convertToDTO(location);

        // Build hierarchy by going up to parent
        List<String> hierarchyPath = new ArrayList<>();
        Location current = location;
        while (current != null) {
            hierarchyPath.add(0, current.getName());
            current = current.getParent();
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponseDTO> searchLocationsByName(String name) {
        List<Location> locations = locationRepository.findByNameContainingIgnoreCase(name);
        return locations.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private LocationResponseDTO convertToDTO(Location location) {
        LocationResponseDTO dto = new LocationResponseDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setCode(location.getCode());
        dto.setType(location.getType());

        if (location.getParent() != null) {
            dto.setParentId(location.getParent().getId());
            dto.setParentName(location.getParent().getName());
        }

        // Set hierarchy fields
        dto.setProvinceName(location.getProvinceName());
        dto.setDistrictName(location.getDistrictName());
        dto.setSectorName(location.getSectorName());
        dto.setCellName(location.getCellName());
        dto.setVillageName(location.getVillageName());
        
        // Build full hierarchy path
        dto.setFullHierarchy(buildFullHierarchy(location));

        return dto;
    }
    
    private void buildHierarchy(Location location, LocationResponseDTO dto) {
        try {
            Location current = location;
            StringBuilder hierarchy = new StringBuilder();
            
            // Traverse up the hierarchy to find all levels
            while (current != null) {
                switch (current.getType()) {
                    case PROVINCE:
                        dto.setProvinceName(current.getName());
                        break;
                    case DISTRICT:
                        dto.setDistrictName(current.getName());
                        break;
                    case SECTOR:
                        dto.setSectorName(current.getName());
                        break;
                    case CELL:
                        dto.setCellName(current.getName());
                        break;
                    case VILLAGE:
                        dto.setVillageName(current.getName());
                        break;
                }
                
                // Build hierarchy string (from top to bottom)
                if (hierarchy.length() > 0) {
                    hierarchy.insert(0, " > ");
                }
                hierarchy.insert(0, current.getName());
                
                current = current.getParent();
            }
            
            dto.setFullHierarchy(hierarchy.toString());
        } catch (Exception e) {
            // If hierarchy building fails, just set the current location name
            dto.setFullHierarchy(location.getName());
        }
    }
    
    private String generateLocationCode(LocationType type, Long parentId) {
        // Get the next sequence number for this type
        long count = locationRepository.countByType(type);
        String prefix = getTypePrefix(type);
        
        // Format: PREFIX + 3-digit number (e.g., KP001, KP002, etc.)
        return String.format("%s%03d", prefix, count + 1);
    }
    
    private String getTypePrefix(LocationType type) {
        switch (type) {
            case PROVINCE:
                return "KP"; // Kigali Province prefix
            case DISTRICT:
                return "KD"; // Kigali District prefix
            case SECTOR:
                return "KS"; // Kigali Sector prefix
            case CELL:
                return "KC"; // Kigali Cell prefix
            case VILLAGE:
                return "KV"; // Kigali Village prefix
            default:
                return "KL"; // Default Kigali Location prefix
        }
    }
    
    private void copyParentHierarchy(Location location, Location parent) {
        location.setProvinceName(parent.getProvinceName());
        location.setDistrictName(parent.getDistrictName());
        location.setSectorName(parent.getSectorName());
        location.setCellName(parent.getCellName());
        location.setVillageName(parent.getVillageName());
    }
    
    private void setLocationHierarchyField(Location location) {
        switch (location.getType()) {
            case PROVINCE:
                location.setProvinceName(location.getName());
                break;
            case DISTRICT:
                location.setDistrictName(location.getName());
                break;
            case SECTOR:
                location.setSectorName(location.getName());
                break;
            case CELL:
                location.setCellName(location.getName());
                break;
            case VILLAGE:
                location.setVillageName(location.getName());
                break;
        }
    }
    
    private String buildFullHierarchy(Location location) {
        List<String> hierarchy = new ArrayList<>();
        Location current = location;
        
        // First, collect all names in reverse order (from current to top)
        while (current != null) {
            hierarchy.add(0, current.getName());
            current = current.getParent();
        }
        
        // Then join them with " > " separator
        return String.join(" > ", hierarchy);
    }
}

