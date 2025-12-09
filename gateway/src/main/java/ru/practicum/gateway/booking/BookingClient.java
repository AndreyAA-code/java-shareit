package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.booking.dto.BookingCreateDto;
import ru.practicum.gateway.booking.dto.BookingState;
import ru.practicum.gateway.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String URL = "/bookings";

    @Value("${server.port}")
    private String host;

    @Autowired
    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createBooking(BookingCreateDto bookingCreateDto, Long userId) {
        return post(host + URL, userId, bookingCreateDto);
    }

    public ResponseEntity<Object> approve(Long userId, Long bookingId, Boolean approved) {
        String path = host + URL + "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, bookingId);
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
