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
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        checkIfEmailExists(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Created user with id {}.", user.getId());
        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
       checkIfIdExists(userId);
       checkIfEmailExists(user);
       users.put(userId, user);
        log.info("Updated user with id {}.", userId);
        return user;
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
        //checkIfEmailExists(User user);
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

    private void checkIfEmailExists(User userNewEmail) {
        if (users.values()
                .stream()
                .filter(user -> !user.getId().equals(userNewEmail.getId()))
                .map(User::getEmail)
                .anyMatch(userNewEmail.getEmail()::equals)) {
            throw new EmailAlreadyExistsException("Email " + userNewEmail.getEmail() + " уже существует");
        }
    }

    private void checkIfIdExists(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }
}
