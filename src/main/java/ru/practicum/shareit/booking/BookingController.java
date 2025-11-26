package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
@Slf4j
public class BookingController {
    BookingService bookingService;  //добавить модификатор!!!!

    @PostMapping
    public BookingDto createBooking(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
       // log.info("!!!!!Create booking: {} and booker {}", bookingCreateDto, userId);
        return bookingService.createBooking(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long bookingId,
                           @RequestParam Boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        return bookingService.findBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) BookingState bookingState) {
        return bookingService.findAllBookings(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestParam(defaultValue = "ALL", required = false) BookingState bookingState) {
        return bookingService.getByOwner(userId, bookingState);
    }

}
