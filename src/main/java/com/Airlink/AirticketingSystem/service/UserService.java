package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.UserResponseDTO;
import com.Airlink.AirticketingSystem.model.enums.Gender;
import com.Airlink.AirticketingSystem.model.enums.LocationType;
import com.Airlink.AirticketingSystem.model.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDTO createUser(String fullName, String email, String phone, 
                               Gender gender, UserRole role, Long locationId);
    
    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByEmail(String email);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    Page<UserResponseDTO> getUsersByRole(UserRole role, Pageable pageable);
    Page<UserResponseDTO> getUsersByLocation(Long locationId, Pageable pageable);
    Page<UserResponseDTO> getUsersByProvinceCode(String provinceCode, Pageable pageable);
    Page<UserResponseDTO> getUsersByProvinceName(String provinceName, Pageable pageable);
    UserResponseDTO updateUser(Long id, String fullName, String phone, Gender gender, 
                              UserRole role, Long locationId);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
}

