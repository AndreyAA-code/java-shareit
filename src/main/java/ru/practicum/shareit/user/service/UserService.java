package ru.practicum.shareit.user.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Collection<UserDto> getUsers() {
        return userRepository.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(User user) {
        return UserMapper.mapToUserDto(userRepository.create(user));
    }

    public UserDto updateUser(@Valid Long userId, @Valid User user) {
        return UserMapper.mapToUserDto(userRepository.updateUser(userId, user));
    }


    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(userRepository.getUserById(userId));
    }
}
