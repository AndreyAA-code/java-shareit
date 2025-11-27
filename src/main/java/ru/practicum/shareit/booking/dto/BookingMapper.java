package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapBookingCreateDtoToBooking(BookingCreateDto bookingCreateDto) {
        return Booking.builder()
                .start(bookingCreateDto.getStart())
                .end(bookingCreateDto.getEnd())
                .build();
    }

    public static BookingDto mapBookingToBookingDto(Booking booking) {
        BookingDto.BookingItemDto itemDto = BookingDto.BookingItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        BookingDto.BookingUserDto bookerDto = BookingDto.BookingUserDto.builder()
                .id(booking.getBooker().getId())
                .name(booking.getBooker().getName())
                .build();

        return BookingDto.builder()
                .id(booking.getId())
                .item(itemDto)
                .booker(bookerDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

}