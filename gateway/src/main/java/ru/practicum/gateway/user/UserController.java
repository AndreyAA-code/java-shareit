package ru.practicum.gateway.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UserCreateDto;
import ru.practicum.gateway.user.dto.UserUpdateDto;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        return userClient.getUser(userId);
    }

    @PostMapping()
    public ResponseEntity<Object> createUser(@RequestBody UserCreateDto userCreateDto) {
        return userClient.createUser(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
                                             @RequestBody UserUpdateDto userUpdateDto) {
        return userClient.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId) {
        return userClient.deleteUser(userId);
    }
}