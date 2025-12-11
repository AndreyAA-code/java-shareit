package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.client.RestTemplateConfig;
import ru.practicum.gateway.user.dto.UserCreateDto;
import ru.practicum.gateway.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {

    private final String url = "/users";

    @Autowired
    public UserClient(RestTemplate restTemplate, RestTemplateConfig config) {
        super(restTemplate, config);
    }

    public ResponseEntity<Object> getUsers() {
        return get(url);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get(url + "/" + userId);
    }

    public ResponseEntity<Object> createUser(UserCreateDto userCreateDto) {
        return post(url, userCreateDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserUpdateDto userUpdateDto) {
        return patch(url + "/" + userId, userId, userUpdateDto);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete(url + "/" + userId);
    }

}