package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping ("")
    public Collection<UserDto> getUsers() {
        log.info("getUsers");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("getUser {} ", userId);
        return userService.getUserById(userId);
    }

    @PostMapping("")
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("createUser");
        return userService.createUser(user);
    }

    @PatchMapping ("/{userId}")
    public UserDto updateUser(@Valid @PathVariable ("userId") Long userId, @RequestBody User user) {
        log.info("patch User {}",userId);
        return userService.updateUser(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Valid @PathVariable ("userId") Long userId) {
        log.info("deleteUser {}", userId);
        userService.deleteUser(userId);
    }
}
