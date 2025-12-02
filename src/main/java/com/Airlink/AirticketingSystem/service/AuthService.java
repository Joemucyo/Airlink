package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.AuthRegisterRequest;
import com.Airlink.AirticketingSystem.dto.AuthResponse;
import com.Airlink.AirticketingSystem.dto.ChangePasswordRequest;
import com.Airlink.AirticketingSystem.dto.LoginRequest;
import com.Airlink.AirticketingSystem.dto.RefreshTokenRequest;
import com.Airlink.AirticketingSystem.dto.UserResponseDTO;
import com.Airlink.AirticketingSystem.exception.BadRequestException;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.RefreshToken;
import com.Airlink.AirticketingSystem.model.User;
import com.Airlink.AirticketingSystem.model.enums.UserRole;
import com.Airlink.AirticketingSystem.repository.RefreshTokenRepository;
import com.Airlink.AirticketingSystem.repository.UserRepository;
import com.Airlink.AirticketingSystem.security.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public AuthResponse register(AuthRegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        savedUser.getEmail(),
                        "",
                        org.springframework.security.core.authority.AuthorityUtils
                                .createAuthorityList("ROLE_" + savedUser.getRole())
                )
        );

        String refreshToken = jwtService.generateRefreshToken(
                new org.springframework.security.core.userdetails.User(
                        savedUser.getEmail(),
                        "",
                        org.springframework.security.core.authority.AuthorityUtils
                                .createAuthorityList("ROLE_" + savedUser.getRole())
                )
        );

        // Save refresh token
        saveRefreshToken(savedUser, refreshToken);

        UserResponseDTO userResponseDTO = convertToDTO(savedUser);

        return new AuthResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationTime(),
                userResponseDTO
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Save refresh token
            saveRefreshToken(user, refreshToken);

            UserResponseDTO userResponseDTO = convertToDTO(user);

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getExpirationTime(),
                    userResponseDTO
            );
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadRequestException("Refresh token has expired");
        }

        User user = refreshToken.getUser();

        if (!user.getIsActive()) {
            throw new BadRequestException("User account is disabled");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "",
                org.springframework.security.core.authority.AuthorityUtils
                        .createAuthorityList("ROLE_" + user.getRole())
        );

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // Delete old refresh token and save new one
        refreshTokenRepository.delete(refreshToken);
        saveRefreshToken(user, newRefreshToken);

        UserResponseDTO userResponseDTO = convertToDTO(user);

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                jwtService.getExpirationTime(),
                userResponseDTO
        );
    }

    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        refreshTokenRepository.delete(refreshToken);
    }

    public UserResponseDTO getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return convertToDTO(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(new java.util.Date());
        userRepository.save(user);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(jwtService.getRefreshTokenExpirationTime() / 1000));
        refreshTokenRepository.save(refreshToken);
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender() != null ? user.getGender().name() : null);
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        return dto;
    }
}
