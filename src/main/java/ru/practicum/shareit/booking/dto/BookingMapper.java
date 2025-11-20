package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public BookingDto toBookingDto(Booking booking) {
       return BookingDto.builder()
               .id(booking.getId())
               .start(booking.getStart())
               .end(booking.getEnd())
               .item(booking.getItem())
               .booker(booking.getBooker())
               .bookingStatus(booking.getBookingStatus())
               .build();
    }

}