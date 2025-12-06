package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserCreateDto;
import ru.practicum.gateway.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {

    private final String url = "/users";

    @Value("${server.host}")
    private String host;

    @Autowired
    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getUsers() {
        return get(host + url);
    }

    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        return get(host + url + "/" + userId);
    }

    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        return post(host + url, userCreateDto);
    }

    public ResponseEntity<Object> updateUser(
            @Valid @PathVariable("userId") Long userId,
            @RequestBody UserUpdateDto userUpdateDto) {
        return patch(host + url + "/" + userId, userId, userUpdateDto);
    }

    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId) {
        return delete(host + url + "/" + userId);
    }
}