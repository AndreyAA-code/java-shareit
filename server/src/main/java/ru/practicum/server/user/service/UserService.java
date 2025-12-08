package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);
}
