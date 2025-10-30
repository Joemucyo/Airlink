package com.Airlink.AirticketingSystem.service;

import com.Airlink.AirticketingSystem.dto.BookingRequestDTO;
import com.Airlink.AirticketingSystem.dto.BookingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO request);
    BookingResponseDTO getBookingById(Long id);
    BookingResponseDTO getBookingByCode(String bookingCode);
    Page<BookingResponseDTO> getAllBookings(Pageable pageable);
    Page<BookingResponseDTO> getBookingsByUser(Long userId, Pageable pageable);
    Page<BookingResponseDTO> getBookingsByFlight(Long flightId, Pageable pageable);
    BookingResponseDTO updateBookingStatus(Long id, String status);
    void cancelBooking(Long id);
    void deleteBooking(Long id);
}

