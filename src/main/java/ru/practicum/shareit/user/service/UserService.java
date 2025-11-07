package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getUsers();

    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}
