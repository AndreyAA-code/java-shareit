package ru.practicum.gateway.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.booking.dto.BookingCreateDto;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingState;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingClient.createBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) BookingState bookingState) {
        return bookingClient.getAllBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) BookingState bookingState) {
        return bookingClient.getByOwner(userId, bookingState);
    }

}
