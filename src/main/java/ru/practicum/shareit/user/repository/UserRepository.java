package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {

    Collection<User> getUsers();

    User create(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    User getUserById(Long userId);

}
