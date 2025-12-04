package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto approve(Long userId, Long bookingId, Boolean approved);

    BookingDto createBooking(BookingCreateDto bookingCreateDto, Long userId);

    BookingDto findBookingById(Long bookingId, Long userId);

    List<BookingDto> findAllBookings(Long userId, BookingState bookingState);

    List<BookingDto> getByOwner(Long userId, BookingState bookingState);

}
