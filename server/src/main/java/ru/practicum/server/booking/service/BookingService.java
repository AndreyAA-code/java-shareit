package ru.practicum.server.booking.service;

import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto createBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingDto findBookingById(Long bookingId, Long userId);

    List<BookingDto> findAllBookings(Long userId, BookingState bookingState);

    List<BookingDto> getByOwner(Long userId, BookingState bookingState);

}
