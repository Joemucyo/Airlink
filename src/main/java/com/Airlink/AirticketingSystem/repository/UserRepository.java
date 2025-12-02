package com.Airlink.AirticketingSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Airlink.AirticketingSystem.model.User;
import com.Airlink.AirticketingSystem.model.enums.LocationType;
import com.Airlink.AirticketingSystem.model.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findAllByRole(UserRole role, Pageable pageable);
    Page<User> findAllByLocation_Id(Long locationId, Pageable pageable);
    
    // Password reset query
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    
    // Email verification query
    Optional<User> findByVerificationToken(String verificationToken);
    
    // Custom query to find users by province code
    @Query("SELECT u FROM User u WHERE u.location.type = :type AND u.location.code = :code")
    Page<User> findByLocationTypeAndCode(@Param("type") LocationType type, @Param("code") String code, Pageable pageable);
    
    // Custom query to find users by province name
    @Query("SELECT u FROM User u WHERE u.location.type = :type AND u.location.name = :name")
    Page<User> findByLocationTypeAndName(@Param("type") LocationType type, @Param("name") String name, Pageable pageable);
}
