package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.booking.dto.BookingCreateDto;
import ru.practicum.gateway.booking.dto.BookingState;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserCreateDto;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.user.dto.UserUpdateDto;

import java.util.Collection;
import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String URL = "/users";

    @Value("${server.host}")
    private String host;

    public UserClient() {
        super(new RestTemplate());
    }

    @GetMapping()
    public Collection<UserDto> getUsers() {
        return null;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return null;
    }

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        return null;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @PathVariable ("userId") Long userId,
                              @RequestBody UserUpdateDto userUpdateDto) {
        return null;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Valid @PathVariable ("userId") Long userId) {

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
