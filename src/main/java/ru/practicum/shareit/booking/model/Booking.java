package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Booking {
    Long id;
    Timestamp start;
    Timestamp end;
    Long item;
    Long booker;
    BookingStatus bookingStatus;
}