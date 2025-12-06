package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UserCreateDto;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.user.dto.UserUpdateDto;

import java.util.Collection;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(path = "/users")
public class UserController {

    @GetMapping ()
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

    @PatchMapping ("/{userId}")
    public UserDto updateUser(@Valid @PathVariable ("userId") Long userId,
                              @RequestBody UserUpdateDto userUpdateDto) {
        return null;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Valid @PathVariable ("userId") Long userId) {

    }
}