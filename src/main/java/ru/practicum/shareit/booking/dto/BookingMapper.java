package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapBookingCreateDtoToBooking(BookingCreateDto bookingCreateDto) {
        return Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .build();
    }

    public static BookingDto mapBookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getBookingStatus())
                .build();
    }

}