package com.Airlink.AirticketingSystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Airlink.AirticketingSystem.model.Flight;
import com.Airlink.AirticketingSystem.model.enums.FlightStatus;
import com.Airlink.AirticketingSystem.model.enums.FlightType;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    
    @Query("SELECT DISTINCT f FROM Flight f " +
           "LEFT JOIN FETCH f.originLocation " +
           "LEFT JOIN FETCH f.destinationLocation " +
           "LEFT JOIN FETCH f.originAirport " +
           "LEFT JOIN FETCH f.destinationAirport " +
           "WHERE f.id = :id")
    Optional<Flight> findByIdWithRelationships(@Param("id") Long id);
    // =============================================
    // Domestic Flights (using Location)
    // =============================================
    
    // Find domestic flights by origin location ID
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'DOMESTIC' AND f.originLocation.id = :locationId")
    Page<Flight> findByOriginLocationId(@Param("locationId") Long locationId, Pageable pageable);
    
    // Find domestic flights by destination location ID
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'DOMESTIC' AND f.destinationLocation.id = :locationId")
    Page<Flight> findByDestinationLocationId(@Param("locationId") Long locationId, Pageable pageable);
    
    // Find domestic flights by origin and destination location IDs
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'DOMESTIC' " +
           "AND f.originLocation.id = :originId AND f.destinationLocation.id = :destinationId")
    Page<Flight> findByOriginLocationIdAndDestinationLocationId(
        @Param("originId") Long originId, 
        @Param("destinationId") Long destinationId, 
        Pageable pageable
    );
    
    // =============================================
    // International Flights (using Airport)
    // =============================================
    
    // Find international flights by origin airport ID
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'INTERNATIONAL' AND f.originAirport.id = :airportId")
    Page<Flight> findByOriginAirportId(@Param("airportId") Long airportId, Pageable pageable);
    
    // Find international flights by destination airport ID
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'INTERNATIONAL' AND f.destinationAirport.id = :airportId")
    Page<Flight> findByDestinationAirportId(@Param("airportId") Long airportId, Pageable pageable);
    
    // Find all flight IDs with pagination
    @Query("SELECT f.id FROM Flight f")
    Page<Long> findAllIds(Pageable pageable);
    
    // Find all flights with relationships by IDs
    @Query("SELECT DISTINCT f FROM Flight f " +
           "LEFT JOIN FETCH f.originLocation " +
           "LEFT JOIN FETCH f.destinationLocation " +
           "LEFT JOIN FETCH f.originAirport " +
           "LEFT JOIN FETCH f.destinationAirport " +
           "WHERE f.id IN :ids")
    List<Flight> findAllWithRelationships(@Param("ids") List<Long> ids);
    
    // Find international flights by origin and destination airport IDs
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'INTERNATIONAL' " +
           "AND f.originAirport.id = :originId AND f.destinationAirport.id = :destinationId")
    Page<Flight> findByOriginAirportIdAndDestinationAirportId(
        @Param("originId") Long originId, 
        @Param("destinationId") Long destinationId, 
        Pageable pageable
    );
    
    // =============================================
    // Generic Search Methods
    // =============================================
    
    // Search flights by query string (searches both domestic and international)
    @Query("SELECT f FROM Flight f WHERE " +
           "(f.flightType = 'DOMESTIC' AND " +
           " (f.originLocation.name LIKE %:query% OR f.originLocation.code LIKE %:query% OR " +
           "  f.destinationLocation.name LIKE %:query% OR f.destinationLocation.code LIKE %:query%)) " +
           "OR " +
           "(f.flightType = 'INTERNATIONAL' AND " +
           " (f.originAirport.name LIKE %:query% OR f.originAirport.code LIKE %:query% OR " +
           "  f.destinationAirport.name LIKE %:query% OR f.destinationAirport.code LIKE %:query% OR " +
           "  f.originAirport.city LIKE %:query% OR f.destinationAirport.city LIKE %:query% OR " +
           "  f.originAirport.country LIKE %:query% OR f.destinationAirport.country LIKE %:query%))")
    Page<Flight> searchFlights(@Param("query") String query, Pageable pageable);
    
    // Search domestic flights by query string
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'DOMESTIC' AND " +
           "(f.originLocation.name LIKE %:query% OR f.originLocation.code LIKE %:query% OR " +
           "f.destinationLocation.name LIKE %:query% OR f.destinationLocation.code LIKE %:query%)")
    Page<Flight> searchDomesticFlights(@Param("query") String query, Pageable pageable);
    
    // Search international flights by query string
    @Query("SELECT f FROM Flight f WHERE f.flightType = 'INTERNATIONAL' AND " +
           "(f.originAirport.name LIKE %:query% OR f.originAirport.code LIKE %:query% OR " +
           "f.destinationAirport.name LIKE %:query% OR f.destinationAirport.code LIKE %:query% OR " +
           "f.originAirport.city LIKE %:query% OR f.destinationAirport.city LIKE %:query% OR " +
           "f.originAirport.country LIKE %:query% OR f.destinationAirport.country LIKE %:query%)")
    Page<Flight> searchInternationalFlights(@Param("query") String query, Pageable pageable);
    
    // =============================================
    // Common Methods
    // =============================================
    
    // Find flights by status
    Page<Flight> findByStatus(FlightStatus status, Pageable pageable);
    
    // Find flights by flight type
    Page<Flight> findByFlightType(FlightType flightType, Pageable pageable);
    
    // Find flights by date range
    Page<Flight> findByDepartureTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // Find flights by flight type and date range
    Page<Flight> findByFlightTypeAndDepartureTimeBetween(
        FlightType flightType, 
        LocalDateTime start, 
        LocalDateTime end, 
        Pageable pageable
    );
    
    // Find all flights ordered by departure time
    Page<Flight> findAllByOrderByDepartureTimeAsc(Pageable pageable);
}
