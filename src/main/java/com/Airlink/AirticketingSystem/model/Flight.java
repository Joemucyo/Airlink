package com.Airlink.AirticketingSystem.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.Airlink.AirticketingSystem.model.enums.FareClass;
import com.Airlink.AirticketingSystem.model.enums.FlightStatus;
import com.Airlink.AirticketingSystem.model.enums.FlightType;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @Column(nullable = false)
    private String airline;

    @Enumerated(EnumType.STRING)
    @Column(name = "flight_type", nullable = false)
    private FlightType flightType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_airport_id")
    private Airport originAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_airport_id")
    private Airport destinationAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_location_id")
    private Location originLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_location_id")
    private Location destinationLocation;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status = FlightStatus.SCHEDULED;

    @Column(nullable = false)
    private double price;

    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FareClassPrice> fareClassPrices = new HashSet<>();

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    // Constructors
    public Flight() {
        // Default constructor
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public Airport getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(Airport originAirport) {
        this.originAirport = originAirport;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(Airport destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public Location getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(Location originLocation) {
        this.originLocation = originLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Set<FareClassPrice> getFareClassPrices() {
        return fareClassPrices;
    }

    public void setFareClassPrices(Set<FareClassPrice> fareClassPrices) {
        this.fareClassPrices = fareClassPrices != null ? fareClassPrices : new HashSet<>();
    }

    public void addFareClassPrice(FareClassPrice fareClassPrice) {
        fareClassPrices.add(fareClassPrice);
        fareClassPrice.setFlight(this);
    }

    public void removeFareClassPrice(FareClassPrice fareClassPrice) {
        fareClassPrices.remove(fareClassPrice);
        fareClassPrice.setFlight(null);
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setFlight(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setFlight(null);
    }

    // Business methods
    public boolean isInternational() {
        return flightType == FlightType.INTERNATIONAL;
    }

    public boolean isDomestic() {
        return flightType == FlightType.DOMESTIC;
    }

    public String getOriginDisplayName() {
        return isInternational() ? String.format("%s (%s)", originAirport.getName(), originAirport.getCode()) : originLocation.getName();
    }

    public String getDestinationDisplayName() {
        return isInternational() ? String.format("%s (%s)", destinationAirport.getName(), destinationAirport.getCode()) : destinationLocation.getName();
    }

    @Override
    public String toString() {
        return String.format("%s %s to %s", 
            flightNumber, 
            isInternational() ? originAirport.getCode() : originLocation.getName(), 
            isInternational() ? destinationAirport.getCode() : destinationLocation.getName()
        );
    }
    
    /**
     * Adds a fare class price to this flight
     * @param fareClass The fare class
     * @param price The price for this fare class
     * @param availableSeats Number of available seats for this fare class
     */
    public void addFareClassPrice(FareClass fareClass, double price, int availableSeats) {
        FareClassPrice fareClassPrice = new FareClassPrice();
        fareClassPrice.setFareClass(fareClass);
        fareClassPrice.setCurrentPrice(price);
        fareClassPrice.setAvailableSeats(availableSeats);
        fareClassPrice.setFlight(this);
        this.fareClassPrices.add(fareClassPrice);
    }
}
