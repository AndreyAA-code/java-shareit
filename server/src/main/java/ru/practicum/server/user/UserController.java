package ru.practicum.server.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserUpdateDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping ()
    public List<UserDto> getUsers() {
        log.info("getUsers");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable Long userId) {
        log.info("getUser {} ", userId);
        return userService.getUserById(userId);
    }

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("createUser");
        return userService.createUser(userCreateDto);
    }

    @PatchMapping ("/{userId}")
    public UserDto updateUser(@Valid @PathVariable ("userId") Long userId,
                              @RequestBody @Valid UserUpdateDto userUpdateDto) {
        log.info("patch User {}",userId);
        return userService.updateUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Valid @PathVariable ("userId") Long userId) {
        log.info("deleteUser {}", userId);
        userService.deleteUser(userId);
    }
}