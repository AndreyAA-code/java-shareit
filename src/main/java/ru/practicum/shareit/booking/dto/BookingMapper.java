package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto mapBookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookingStatus(booking.getBookingStatus())
                .build();
    }

    public static BookingCreateDto mapBookingToBookingCreateDto(Booking booking) {
       return BookingCreateDto.builder()
               .itemId(booking.getItem().getId())
               .bookerId(booking.getBooker().getId())
               .start(booking.getStart())
               .end(booking.getEnd())
               .bookingStatus(booking.getBookingStatus())
               .build();
    }

    public static Booking mapBookingDtoToBooking(BookingCreateDto bookingCreateDto) {
        return Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .bookingStatus(bookingCreateDto.getBookingStatus())
                .build();
    }

}