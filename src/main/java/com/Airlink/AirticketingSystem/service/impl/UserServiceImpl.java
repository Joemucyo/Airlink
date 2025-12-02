package com.Airlink.AirticketingSystem.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Airlink.AirticketingSystem.dto.LocationResponseDTO;
import com.Airlink.AirticketingSystem.dto.UserRequestDTO;
import com.Airlink.AirticketingSystem.dto.UserResponseDTO;
import com.Airlink.AirticketingSystem.exception.BadRequestException;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.Location;
import com.Airlink.AirticketingSystem.model.User;
import com.Airlink.AirticketingSystem.model.enums.Gender;
import com.Airlink.AirticketingSystem.model.enums.LocationType;
import com.Airlink.AirticketingSystem.model.enums.UserRole;
import com.Airlink.AirticketingSystem.repository.LocationRepository;
import com.Airlink.AirticketingSystem.repository.UserRepository;
import com.Airlink.AirticketingSystem.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO createUser(String fullName, String email, String phone,
                                    Gender gender, UserRole role, Long locationId) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }

        // Validate location is a village
        Location location = null;
        if (locationId != null) {
            location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location", locationId));
            
            if (location.getType() != LocationType.VILLAGE) {
                throw new BadRequestException("Location must be a village");
            }
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setGender(gender);
        user.setRole(role);
        user.setLocation(location);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    /**
     * Create user from UserRequestDTO with password encryption (for admin user creation)
     */
    public UserResponseDTO createUserWithPassword(UserRequestDTO request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        // Validate location is a village
        Location location = null;
        if (request.getLocationId() != null) {
            location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));
            
            if (location.getType() != LocationType.VILLAGE) {
                throw new BadRequestException("Location must be a village");
            }
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setRole(request.getRole());
        user.setLocation(location);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findAllByRole(role, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersByLocation(Long locationId, Pageable pageable) {
        return userRepository.findAllByLocation_Id(locationId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersByProvinceCode(String provinceCode, Pageable pageable) {
        return userRepository.findByLocationTypeAndCode(LocationType.PROVINCE, provinceCode, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersByProvinceName(String provinceName, Pageable pageable) {
        return userRepository.findByLocationTypeAndName(LocationType.PROVINCE, provinceName, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public UserResponseDTO updateUser(Long id, String fullName, String phone, Gender gender,
                                     UserRole role, Long locationId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (fullName != null) user.setFullName(fullName);
        if (phone != null) user.setPhone(phone);
        if (gender != null) user.setGender(gender);
        if (role != null) user.setRole(role);

        if (locationId != null) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location", locationId));
            
            if (location.getType() != LocationType.VILLAGE) {
                throw new BadRequestException("Location must be a village");
            }
            
            user.setLocation(location);
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);

        if (user.getLocation() != null) {
            LocationResponseDTO locationDTO = convertLocationToDTO(user.getLocation());
            dto.setLocation(locationDTO);
        }

        return dto;
    }

    private LocationResponseDTO convertLocationToDTO(Location location) {
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
    
    private String buildFullHierarchy(Location location) {
        if (location == null) {
            return "";
        }
        
        StringBuilder path = new StringBuilder(location.getName());
        Location current = location.getParent();
        
        while (current != null) {
            path.insert(0, current.getName() + " > ");
            current = current.getParent();
        }
        
        return path.toString();
    }
}

