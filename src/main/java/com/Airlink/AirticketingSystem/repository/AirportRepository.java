package com.Airlink.AirticketingSystem.repository;

import com.Airlink.AirticketingSystem.model.Airport;
import com.Airlink.AirticketingSystem.model.enums.AirportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    Optional<Airport> findByCode(String code);
    boolean existsByCode(String code);
    
    // Search by name or code
    Page<Airport> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code, Pageable pageable);
    
    // Filter by airport type
    Page<Airport> findByType(AirportType type, Pageable pageable);
    
    // Filter by city
    Page<Airport> findByCityIgnoreCase(String city, Pageable pageable);
    
    // Filter by country
    Page<Airport> findByCountryIgnoreCase(String country, Pageable pageable);
    
    // Find airports within a certain radius (in kilometers)
    @Query("SELECT a FROM Airport a WHERE " +
           "6371 * acos(" +
           "cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(a.latitude))" +
           ") <= :radiusKm")
    List<Airport> findNearbyAirports(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm
    );
    
    // Check if airport with same name and city exists (for validation)
    boolean existsByNameAndCityIgnoreCase(String name, String city);
}
