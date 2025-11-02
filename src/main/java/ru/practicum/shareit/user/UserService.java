package ru.practicum.shareit.user;

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
}
