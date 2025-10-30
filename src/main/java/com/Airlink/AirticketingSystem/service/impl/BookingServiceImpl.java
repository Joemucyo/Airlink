package com.Airlink.AirticketingSystem.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Airlink.AirticketingSystem.dto.BookingRequestDTO;
import com.Airlink.AirticketingSystem.dto.BookingResponseDTO;
import com.Airlink.AirticketingSystem.dto.FlightResponseDTO;
import com.Airlink.AirticketingSystem.dto.PassengerRequestDTO;
import com.Airlink.AirticketingSystem.dto.PassengerResponseDTO;
import com.Airlink.AirticketingSystem.dto.UserResponseDTO;
import com.Airlink.AirticketingSystem.exception.BadRequestException;
import com.Airlink.AirticketingSystem.exception.ResourceNotFoundException;
import com.Airlink.AirticketingSystem.model.Booking;
import com.Airlink.AirticketingSystem.model.Flight;
import com.Airlink.AirticketingSystem.model.Passenger;
import com.Airlink.AirticketingSystem.model.User;
import com.Airlink.AirticketingSystem.model.enums.BookingStatus;
import com.Airlink.AirticketingSystem.model.enums.PaymentStatus;
import com.Airlink.AirticketingSystem.repository.BookingRepository;
import com.Airlink.AirticketingSystem.repository.FlightRepository;
import com.Airlink.AirticketingSystem.repository.PassengerRepository;
import com.Airlink.AirticketingSystem.repository.UserRepository;
import com.Airlink.AirticketingSystem.repository.PaymentRepository;
import com.Airlink.AirticketingSystem.model.Payment;
import com.Airlink.AirticketingSystem.dto.PaymentResponseDTO;
import com.Airlink.AirticketingSystem.service.BookingService;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        // Validate flight
        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", request.getFlightId()));

        // Validate user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));

        // Check if flight has enough available seats
        int passengersCount = request.getPassengers() != null ? request.getPassengers().size() : 1;
        if (flight.getAvailableSeats() < passengersCount) {
            throw new BadRequestException("Not enough available seats on this flight");
        }

        // Generate unique booking code
        String bookingCode = generateUniqueBookingCode();

        // Create booking
        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setBookingDate(LocalDateTime.now());
        booking.setFlight(flight);
        booking.setUser(user);
        booking.setTotalAmount(request.getTotalAmount());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        // Create passengers and attach to booking
        List<Passenger> savedPassengers = new ArrayList<>();
        if (request.getPassengers() != null && !request.getPassengers().isEmpty()) {
            for (PassengerRequestDTO passengerDTO : request.getPassengers()) {
                Passenger passenger = new Passenger();
                passenger.setFirstName(passengerDTO.getFirstName());
                passenger.setLastName(passengerDTO.getLastName());
                passenger.setPassportNumber(passengerDTO.getPassportNumber());
                passenger.setDateOfBirth(passengerDTO.getDateOfBirth());
                passenger.setGender(passengerDTO.getGender());
                passenger.setBooking(savedBooking);

                Passenger savedPassenger = passengerRepository.save(passenger);
                savedPassengers.add(savedPassenger);
            }
            // Ensure booking has the passengers list populated
            savedBooking.setPassengers(savedPassengers);
            bookingRepository.save(savedBooking);
        }

        // Update flight available seats
        flight.setAvailableSeats(flight.getAvailableSeats() - passengersCount);
        flightRepository.save(flight);

        return convertToDTO(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findWithPaymentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        return convertToDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingByCode(String bookingCode) {
        Booking booking = bookingRepository.findWithPaymentByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking with code: " + bookingCode));
        return convertToDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> getBookingsByUser(Long userId, Pageable pageable) {
        return bookingRepository.findByUser_Id(userId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponseDTO> getBookingsByFlight(Long flightId, Pageable pageable) {
        return bookingRepository.findByFlight_Id(flightId, pageable)
                .map(this::convertToDTO);
    }

    @Override
    public BookingResponseDTO updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        booking.setStatus(BookingStatus.valueOf(status));
        Booking updatedBooking = bookingRepository.save(booking);

        return convertToDTO(updatedBooking);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));

        // Restore flight seats
        Flight flight = booking.getFlight();
        int passengersCount = booking.getPassengers() != null ? booking.getPassengers().size() : 0;
        flight.setAvailableSeats(flight.getAvailableSeats() + passengersCount);
        flightRepository.save(flight);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", id));
        bookingRepository.delete(booking);
    }

    private String generateUniqueBookingCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + 
                   UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (bookingRepository.existsByBookingCode(code));
        return code;
    }

    private BookingResponseDTO convertToDTO(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        
        try {
            // Get the booking with its relationships including payment
            Booking managedBooking = bookingRepository.findWithPaymentById(booking.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking", booking.getId()));
            
            // Create the DTO with the booking data
            BookingResponseDTO dto = new BookingResponseDTO();
            dto.setId(managedBooking.getId());
            dto.setBookingCode(managedBooking.getBookingCode());
            dto.setBookingDate(managedBooking.getBookingDate());
            dto.setTotalAmount(managedBooking.getTotalAmount());
            
            // Set status from managed entity
            if (managedBooking.getStatus() != null) {
                dto.setStatus(managedBooking.getStatus().name());
            } else {
                dto.setStatus(BookingStatus.PENDING.name());
            }

            // Convert flight if present
            if (managedBooking.getFlight() != null) {
                dto.setFlight(modelMapper.map(managedBooking.getFlight(), FlightResponseDTO.class));
            }

            // Convert user if present
            if (managedBooking.getUser() != null) {
                dto.setUser(modelMapper.map(managedBooking.getUser(), UserResponseDTO.class));
            }

            // Convert passengers if present
            if (managedBooking.getPassengers() != null && !managedBooking.getPassengers().isEmpty()) {
                dto.setPassengers(managedBooking.getPassengers().stream()
                        .map(this::convertPassengerToDTO)
                        .toList());
            }

            // Set payment if present
            if (managedBooking.getPayment() != null) {
                dto.setPayment(modelMapper.map(managedBooking.getPayment(), PaymentResponseDTO.class));
            }

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error converting booking to DTO", e);
        }
    }

    private PassengerResponseDTO convertPassengerToDTO(Passenger passenger) {
        return modelMapper.map(passenger, PassengerResponseDTO.class);
    }
}
