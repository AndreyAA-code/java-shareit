package ru.practicum.server.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.server.exceptions.EmailAlreadyExistsException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserMapper;
import ru.practicum.server.user.dto.UserUpdateDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Пользователь с email " + userCreateDto.getEmail() + " уже существует");
        }
        User user = UserMapper.mapToUser(userCreateDto);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User existingUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + "doesn't exist"));
        if (userRepository.findByEmail(userUpdateDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Пользователь с email " + userUpdateDto.getEmail() + " уже существует");
        }
        User updatedUser = UserMapper.mapToUserFields(existingUser, userUpdateDto);
        return UserMapper.mapToUserDto(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + "doesn't exist")));
    }
}