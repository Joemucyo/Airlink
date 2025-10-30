package com.Airlink.AirticketingSystem.model;

import java.util.List;
import jakarta.persistence.*;
import com.Airlink.AirticketingSystem.model.enums.AirportType;

@Entity
@Table(name = "airports")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false, length = 3)
    private String code;  // IATA code (e.g., KGL, JFK, LHR)

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AirportType type;

    // Location fields
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String country;
    
    @Column(name = "iata_city_code", length = 3)
    private String iataCityCode;  // e.g., NYC for New York City
    
    private Double latitude;
    private Double longitude;
    
    @Column(length = 50)
    private String timezone;  // e.g., "Africa/Kigali", "America/New_York"

    // Relationships
    @OneToMany(mappedBy = "originAirport", fetch = FetchType.LAZY)
    private List<Flight> departingFlights;

    @OneToMany(mappedBy = "destinationAirport", fetch = FetchType.LAZY)
    private List<Flight> arrivingFlights;

    // Constructors
    public Airport() {}

    public Airport(String name, String code, String description, AirportType type, 
                  String city, String country, String iataCityCode, 
                  Double latitude, Double longitude, String timezone) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.type = type;
        this.city = city;
        this.country = country;
        this.iataCityCode = iataCityCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AirportType getType() {
        return type;
    }

    public void setType(AirportType type) {
        this.type = type;
    }

    // Location getters and setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIataCityCode() {
        return iataCityCode;
    }

    public void setIataCityCode(String iataCityCode) {
        this.iataCityCode = iataCityCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    // Flight relationships
    public List<Flight> getDepartingFlights() {
        return departingFlights;
    }

    public void setDepartingFlights(List<Flight> departingFlights) {
        this.departingFlights = departingFlights;
    }

    public List<Flight> getArrivingFlights() {
        return arrivingFlights;
    }

    public void setArrivingFlights(List<Flight> arrivingFlights) {
        this.arrivingFlights = arrivingFlights;
    }

    // Helper methods
    @Override
    public String toString() {
        return "Airport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
