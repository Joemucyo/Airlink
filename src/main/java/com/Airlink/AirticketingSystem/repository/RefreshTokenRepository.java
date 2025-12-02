package com.Airlink.AirticketingSystem.repository;

import com.Airlink.AirticketingSystem.model.RefreshToken;
import com.Airlink.AirticketingSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    void deleteByUser(User user);
    
    int deleteByUserAndToken(User user, String token);
}
