package ru.practicum.gateway.booking;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.booking.dto.BookingCreateDto;
import ru.practicum.gateway.booking.dto.BookingDto;
import ru.practicum.gateway.booking.dto.BookingState;
import ru.practicum.gateway.client.BaseClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String URL = "/bookings";

    @Value("${server.host}")
    private String host;

    public BookingClient() {
        super(new RestTemplate());
    }

    public ResponseEntity<Object> createBooking(BookingCreateDto bookingCreateDto, Long userId) {
        return post(host + URL, userId, bookingCreateDto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        String path = host + URL + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        String path = host + URL + "/" + bookingId;
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllBookings(Long userId, BookingState bookingState) {
        Map<String, Object> params = Map.of("state", bookingState);
        return get(host + URL, userId, params);
    }

    public ResponseEntity<Object> getByOwner(Long userId, BookingState bookingState) {
        String path = host + URL + "/owner";
        Map<String, Object> params = Map.of("state", bookingState);
        return get(path, userId, params);
    }

}
