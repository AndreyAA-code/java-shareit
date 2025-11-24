package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.Optional;

@Service
public interface BookingService {

    BookingDto save(BookingCreateDto booking, Long userId);

    BookingDto findById(Long bookingId);
}
