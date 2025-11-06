package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = UserMapper.mapToUser(userCreateDto);
        return UserMapper.mapToUserDto(userRepository.create(user));
    }

    @Override
    public UserDto updateUser(@Valid Long userId, @Valid UserUpdateDto userUpdateDto) {
        User updatedUser = userRepository.getUserById(userId);
        UserMapper.mapToUserFields(updatedUser,userUpdateDto);
        return UserMapper.mapToUserDto(userRepository.updateUser(userId, updatedUser));
    }


    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(userRepository.getUserById(userId));
    }
}
