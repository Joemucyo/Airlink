package com.Airlink.AirticketingSystem.model;

import com.Airlink.AirticketingSystem.model.enums.FareClass;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fare_class_prices")
@Data
public class FareClassPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FareClass fareClass;
    
    @Column(nullable = false)
    private double basePrice;
    
    private double currentPrice;
    
    @Column(nullable = false)
    private int availableSeats;
}
