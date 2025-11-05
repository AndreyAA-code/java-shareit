package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Value
@Builder
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id;
        Timestamp start;
        Timestamp end;
        Long item;
        Long booker;
        BookingStatus bookingStatus;
}
