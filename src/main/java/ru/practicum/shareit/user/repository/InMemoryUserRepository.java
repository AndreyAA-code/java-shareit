package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        checkIfEmailExists(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Created user with id {}.", user.getId());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        checkIfIdExists(userId);
        User newUser = users.get(userId);
        if (!(user.getEmail() == null)) {
            checkIfEmailExists(user.getEmail());
            newUser.setEmail(user.getEmail());
        }
        newUser.setName(user.getName());
        log.info("Updated user with id {}.", newUser.getId());
        return newUser;
    }

    @Override
    public void deleteUser(Long userId) {
        checkIfIdExists(userId);
        users.remove(userId);
        log.info("Deleted user with id {}.", userId);
    }

    @Override
    public User getUserById(Long userId) {
        checkIfIdExists(userId);
        log.info("Get user with id {}.", userId);
        return users.get(userId);
    }

    private Long getNextId() {
        Long maxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.info("Max id = {}.", maxId);
        return ++maxId;
    }

    private void checkIfEmailExists(String email) {
        if (users.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(email::equals)) {
            throw new EmailAlreadyExistsException("Email " + email + " уже существует");
        }
    }

    private void checkIfIdExists(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }
}
