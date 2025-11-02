package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UserRepository {
     public Collection<User> getUsers();
}
