package ru.practicum.shareit.user.dto;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
public class InMemoryUserRepository implements UserRepository {
    Map<Long,User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values()
                .stream()
                .collect(Collectors.toList());
    }
}