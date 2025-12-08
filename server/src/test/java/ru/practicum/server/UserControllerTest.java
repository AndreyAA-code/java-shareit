package ru.practicum.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.user.UserController;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserUpdateDto;
import ru.practicum.server.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;

    @Test
    void shouldGetAllUsers() throws Exception {
        UserDto userDto = new UserDto(userId, "test@example.com", "John");

        when(userService.getUsers()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].name").value("John"));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void shouldGetUserById() throws Exception {
        UserDto userDto = new UserDto(userId, "test@example.com", "John");

        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("John"));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserCreateDto createDto = new UserCreateDto("John", "test@example.com");
        UserDto savedDto = new UserDto(userId, "test@example.com", "John");

        when(userService.createUser(any(UserCreateDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("John"));

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto("Jane", "jane@example.com");
        UserDto updatedDto = new UserDto(userId, "jane@example.com", "Jane");

        when(userService.updateUser(eq(userId), any(UserUpdateDto.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.name").value("Jane"));

        verify(userService, times(1)).updateUser(eq(userId), any(UserUpdateDto.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        when(userService.getUserById(999L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void shouldValidateOnCreateUserWithInvalidEmail() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto("John", "bad-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    void shouldValidateOnUpdateUserWithInvalidEmail() throws Exception {
        UserUpdateDto invalidDto = new UserUpdateDto("Jane", "bad-email");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(any(), any());
    }
}
