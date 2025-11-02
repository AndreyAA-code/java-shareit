package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserRepository {

    Collection<User> getUsers();

    User create(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    User getUserById(Long userId);

}
