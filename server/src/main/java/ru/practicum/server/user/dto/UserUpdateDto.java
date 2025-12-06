package ru.practicum.server.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String name;
    @Email
    @NotBlank
    private String email;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

}