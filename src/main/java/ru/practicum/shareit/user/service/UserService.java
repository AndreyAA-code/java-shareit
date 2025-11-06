package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getUsers();

    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(@Valid Long userId, @Valid UserUpdateDto userUpdateDto);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}
