package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.model.Flight;
import com.Airlink.AirticketingSystem.model.FareClassPrice;
import com.Airlink.AirticketingSystem.model.enums.FareClass;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PricingService {
    
    private static final double DEMAND_FACTOR = 0.1; // 10% increase per demand level
    private static final double TIME_FACTOR = 0.05; // 5% increase per week closer to departure
    
    public double calculateDynamicPrice(Flight flight, FareClass fareClass, int passengerCount) {
        FareClassPrice price = flight.getFareClassPrices().stream()
                .filter(p -> p.getFareClass() == fareClass)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fare class not available for this flight"));
        
        double basePrice = price.getBasePrice();
        double dynamicPrice = basePrice;
        
        // Adjust for time before departure (closer to departure = higher price)
        long daysUntilDeparture = ChronoUnit.DAYS.between(
            LocalDate.now(), 
            flight.getDepartureTime().toLocalDate()
        );
        
        if (daysUntilDeparture < 7) {
            dynamicPrice *= 1.3; // 30% more expensive if less than a week away
        } else if (daysUntilDeparture < 30) {
            dynamicPrice *= 1.15; // 15% more expensive if less than a month away
        }
        
        // Adjust for seat availability (fewer seats left = higher price)
        double seatRatio = (double) price.getAvailableSeats() / flight.getTotalCapacity();
        if (seatRatio < 0.2) { // Less than 20% seats left
            dynamicPrice *= 1.2; // 20% more expensive
        } else if (seatRatio < 0.5) { // Less than 50% seats left
            dynamicPrice *= 1.1; // 10% more expensive
        }
        
        // Adjust for number of passengers (discount for group bookings)
        if (passengerCount >= 10) {
            dynamicPrice *= 0.9; // 10% discount for groups of 10 or more
        } else if (passengerCount >= 4) {
            dynamicPrice *= 0.95; // 5% discount for groups of 4-9
        }
        
        // Ensure price doesn't go below base price
        return Math.max(basePrice, Math.round(dynamicPrice * 100.0) / 100.0);
    }
}
