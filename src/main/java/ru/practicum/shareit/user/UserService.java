package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Collection<User> getUsers() {
        return userRepository.getUsers();
    }

    public User createUser(User user) {
        return userRepository.create(user);
    }

    public User updateUser(@Valid Long userId, @Valid User user) {
        return userRepository.updateUser(userId, user);
    }


    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }
}
