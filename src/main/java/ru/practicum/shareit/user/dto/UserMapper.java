package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User mapToUser(UserCreateDto userCreateDto) {
        return User.builder()
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .build();
    }

    public static User mapToUserFields(User user, UserUpdateDto userUpdateDto) {

        if (userUpdateDto.hasName()) {
            user.setName(userUpdateDto.getName());
        }
        if (userUpdateDto.hasEmail()) {
            user.setEmail(userUpdateDto.getEmail());
        }
        return user;
    }
}
