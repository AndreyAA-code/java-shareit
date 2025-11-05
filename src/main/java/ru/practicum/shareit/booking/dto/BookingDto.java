package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.sql.Timestamp;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Timestamp start;
    private Timestamp end;
    private Long item;
    private Long booker;
    private BookingStatus bookingStatus;
}
