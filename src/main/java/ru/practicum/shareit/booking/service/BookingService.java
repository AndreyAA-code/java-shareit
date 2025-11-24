package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    BookingDto createBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingDto findBookingById(Long bookingId, Long userId);

    List<BookingDto> findAllBookings();
}
