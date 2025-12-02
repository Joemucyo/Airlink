package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.AuthRegisterRequest;
import com.Airlink.AirticketingSystem.dto.AuthResponse;
import com.Airlink.AirticketingSystem.dto.ChangePasswordRequest;
import com.Airlink.AirticketingSystem.dto.ForgotPasswordRequest;
import com.Airlink.AirticketingSystem.dto.LoginRequest;
import com.Airlink.AirticketingSystem.dto.RefreshTokenRequest;
import com.Airlink.AirticketingSystem.dto.ResendVerificationRequest;
import com.Airlink.AirticketingSystem.dto.ResetPasswordRequest;
import com.Airlink.AirticketingSystem.dto.UserResponseDTO;
import com.Airlink.AirticketingSystem.dto.VerifyEmailRequest;
import com.Airlink.AirticketingSystem.exception.BadRequestException;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.RefreshToken;
import com.Airlink.AirticketingSystem.model.User;
import com.Airlink.AirticketingSystem.model.enums.UserRole;
import com.Airlink.AirticketingSystem.repository.RefreshTokenRepository;
import com.Airlink.AirticketingSystem.repository.UserRepository;
import com.Airlink.AirticketingSystem.security.JwtService;
import com.Airlink.AirticketingSystem.security.TokenUtil;
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

    @Autowired
    private TokenUtil tokenUtil;

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
        user.setEmailVerified(false);
        
        // Generate verification token
        String verificationToken = tokenUtil.generateToken();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(tokenUtil.getEmailVerificationTokenExpiration());

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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // Check if account is locked
        if (user.getLockoutUntil() != null && Instant.now().isBefore(user.getLockoutUntil())) {
            throw new BadRequestException("Account is locked. Try again later.");
        }

        // Check if email is verified
        if (!user.getEmailVerified()) {
            throw new BadRequestException("Please verify your email before logging in. Check your email for verification link.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Reset failed attempts on successful login
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            // Save refresh token
            saveRefreshToken(user, refreshToken);
            userRepository.save(user);

            UserResponseDTO userResponseDTO = convertToDTO(user);

            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    jwtService.getExpirationTime(),
                    userResponseDTO
            );
        } catch (Exception e) {
            // Increment failed login attempts
            user.setFailedLoginAttempts((user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1);

            // Lock account after 5 failed attempts for 15 minutes
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockoutUntil(Instant.now().plusSeconds(900)); // 15 minutes
            }

            userRepository.save(user);
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

    // ============ Password Reset Methods ============

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with email: " + request.getEmail()));

        String resetToken = tokenUtil.generateToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(tokenUtil.getPasswordResetTokenExpiration());
        userRepository.save(user);

        // In a real application, send email with reset token here
        // For now, just save the token to database
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = userRepository.findByPasswordResetToken(request.getResetToken())
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (user.getPasswordResetTokenExpiry() == null || tokenUtil.isTokenExpired(user.getPasswordResetTokenExpiry())) {
            throw new BadRequestException("Reset token has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }

    // ============ Email Verification Methods ============

    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByVerificationToken(request.getVerificationToken())
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        if (user.getVerificationTokenExpiry() == null || tokenUtil.isTokenExpired(user.getVerificationTokenExpiry())) {
            throw new BadRequestException("Verification token has expired. Please request a new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    public void resendVerification(ResendVerificationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with email: " + request.getEmail()));

        if (user.getEmailVerified()) {
            throw new BadRequestException("Email is already verified");
        }

        String verificationToken = tokenUtil.generateToken();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(tokenUtil.getEmailVerificationTokenExpiration());
        userRepository.save(user);

        // In a real application, send email with verification token here
        // For now, just save the token to database
    }
}
