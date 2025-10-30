package com.Airlink.AirticketingSystem.dto;

import com.Airlink.AirticketingSystem.model.enums.FareClass;
import com.Airlink.AirticketingSystem.model.enums.FlightType;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

public class FlightResponseDTO {
    private Long id;
    private String flightNumber;
    private String airline;
    private FlightType flightType;
    
    // For domestic flights
    private Long originLocationId;
    private String originLocationName;
    private String originLocationCode;
    private Long destinationLocationId;
    private String destinationLocationName;
    private String destinationLocationCode;
    
    // For international flights
    private Long originAirportId;
    private String originAirportName;
    private String originAirportCode;
    private String originAirportCity;
    private String originAirportCountry;
    private Long destinationAirportId;
    private String destinationAirportName;
    private String destinationAirportCode;
    private String destinationAirportCity;
    private String destinationAirportCountry;
    
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;
    private Double price;
    private Integer totalCapacity;
    private int availableSeats;
    
    private Map<FareClass, Double> fareClassPrices = new EnumMap<>(FareClass.class);
    private Map<FareClass, Integer> availableSeatsPerClass = new EnumMap<>(FareClass.class);

    public FlightResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    // Flight Type
    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) { this.flightType = flightType; }
    
    // Domestic flight getters and setters
    public Long getOriginLocationId() { return originLocationId; }
    public void setOriginLocationId(Long originLocationId) { this.originLocationId = originLocationId; }
    
    public String getOriginLocationName() { return originLocationName; }
    public void setOriginLocationName(String originLocationName) { this.originLocationName = originLocationName; }
    
    public String getOriginLocationCode() { return originLocationCode; }
    public void setOriginLocationCode(String originLocationCode) { this.originLocationCode = originLocationCode; }
    
    public Long getDestinationLocationId() { return destinationLocationId; }
    public void setDestinationLocationId(Long destinationLocationId) { this.destinationLocationId = destinationLocationId; }
    
    public String getDestinationLocationName() { return destinationLocationName; }
    public void setDestinationLocationName(String destinationLocationName) { this.destinationLocationName = destinationLocationName; }
    
    public String getDestinationLocationCode() { return destinationLocationCode; }
    public void setDestinationLocationCode(String destinationLocationCode) { this.destinationLocationCode = destinationLocationCode; }
    
    // International flight getters and setters
    public Long getOriginAirportId() { return originAirportId; }
    public void setOriginAirportId(Long originAirportId) { this.originAirportId = originAirportId; }
    
    public String getOriginAirportName() { return originAirportName; }
    public void setOriginAirportName(String originAirportName) { this.originAirportName = originAirportName; }
    
    public String getOriginAirportCode() { return originAirportCode; }
    public void setOriginAirportCode(String originAirportCode) { this.originAirportCode = originAirportCode; }
    
    public String getOriginAirportCity() { return originAirportCity; }
    public void setOriginAirportCity(String originAirportCity) { this.originAirportCity = originAirportCity; }
    
    public String getOriginAirportCountry() { return originAirportCountry; }
    public void setOriginAirportCountry(String originAirportCountry) { this.originAirportCountry = originAirportCountry; }
    
    public Long getDestinationAirportId() { return destinationAirportId; }
    public void setDestinationAirportId(Long destinationAirportId) { this.destinationAirportId = destinationAirportId; }
    
    public String getDestinationAirportName() { return destinationAirportName; }
    public void setDestinationAirportName(String destinationAirportName) { this.destinationAirportName = destinationAirportName; }
    
    public String getDestinationAirportCode() { return destinationAirportCode; }
    public void setDestinationAirportCode(String destinationAirportCode) { this.destinationAirportCode = destinationAirportCode; }
    
    public String getDestinationAirportCity() { return destinationAirportCity; }
    public void setDestinationAirportCity(String destinationAirportCity) { this.destinationAirportCity = destinationAirportCity; }
    
    public String getDestinationAirportCountry() { return destinationAirportCountry; }
    public void setDestinationAirportCountry(String destinationAirportCountry) { this.destinationAirportCountry = destinationAirportCountry; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
    
    public Map<FareClass, Double> getFareClassPrices() {
        return fareClassPrices;
    }
    
    public void setFareClassPrices(Map<FareClass, Double> fareClassPrices) {
        this.fareClassPrices = fareClassPrices != null ? new EnumMap<>(fareClassPrices) : new EnumMap<>(FareClass.class);
    }
    
    public Map<FareClass, Integer> getAvailableSeatsPerClass() {
        return availableSeatsPerClass;
    }
    
    public void setAvailableSeatsPerClass(Map<FareClass, Integer> availableSeatsPerClass) {
        this.availableSeatsPerClass = availableSeatsPerClass != null ? new EnumMap<>(availableSeatsPerClass) : new EnumMap<>(FareClass.class);
    }
}
