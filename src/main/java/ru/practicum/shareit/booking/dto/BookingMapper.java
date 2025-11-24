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
        BookingItemDto itemDto = BookingItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();

        BookingUserDto bookerDto = BookingUserDto.builder()
                .id(booking.getBooker().getId())
                .build();

        return BookingDto.builder()
                .id(booking.getId())
                .item(itemDto)
                .booker(bookerDto)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getBookingStatus())
                .build();
    }

}