package ru.practicum.server.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.user.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();

    User save(User user);

    void deleteById(Long userId);

    Optional<User> getUserById(Long userId);

    Optional<User> findByEmail(String email);

}
