package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingCreateDto bookingCreateDto,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        log.info("!!!!!Create booking: {}", bookingCreateDto);
        return bookingService.save(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking booking(@PathVariable Long bookingId) {
        return null;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.findById(bookingId);
    }


}
