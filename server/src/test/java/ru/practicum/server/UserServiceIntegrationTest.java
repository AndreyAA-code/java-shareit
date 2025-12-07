package ru.practicum.server;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exceptions.EmailAlreadyExistsException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.dto.UserUpdateDto;
import ru.practicum.server.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        UserDto saved = userService.createUser(dto);
        testUserId = saved.getId();
    }

    @AfterEach
    void tearDown() {
        if (testUserId != null) {
            userService.deleteUser(testUserId);
            testUserId = null;
        }
    }

    @Test
    void testGetUsers_ShouldReturnAllUsers() {

        Collection<UserDto> usersCollection = userService.getUsers();
        List<UserDto> users = new ArrayList<>(usersCollection);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(testUserId, users.get(0).getId());
        assertEquals("Test User", users.get(0).getName());
        assertEquals("test@example.com", users.get(0).getEmail());
    }

    @Test
    void testCreateUser_ValidData_ShouldSucceed() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("New User");
        dto.setEmail("new@example.com");

        UserDto created = userService.createUser(dto);

        assertNotNull(created.getId());
        assertEquals("New User", created.getName());
        assertEquals("new@example.com", created.getEmail());
    }

    @Test
    void testCreateUser_DuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Duplicate");
        dto.setEmail("test@example.com");

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.createUser(dto)
        );

        assertTrue(exception.getMessage().contains("test@example.com"));
        assertTrue(exception.getMessage().contains("уже существует"));
    }

    @Test
    void testCreateUser_EmailNull_ShouldThrowException() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName("No Email");
        dto.setEmail(null);

        assertThrows(Exception.class, () -> userService.createUser(dto));
    }

    @Test
    void testCreateUser_NameNull_ShouldThrowException() {
        UserCreateDto dto = new UserCreateDto();
        dto.setName(null);
        dto.setEmail("name@example.com");

        assertThrows(Exception.class, () -> userService.createUser(dto));
    }

    @Test
    void testUpdateUser_ValidData_ShouldSucceed() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated Name");
        dto.setEmail("updated@example.com");

        UserDto updated = userService.updateUser(testUserId, dto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void testUpdateUser_DuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        UserCreateDto dto2 = new UserCreateDto();
        dto2.setName("Second User");
        dto2.setEmail("second@example.com");
        UserDto secondUser = userService.createUser(dto2);

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("second@example.com");

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.updateUser(testUserId, updateDto)
        );

        assertTrue(exception.getMessage().contains("second@example.com"));

        userService.deleteUser(secondUser.getId());
    }

    @Test
    void testUpdateUser_NonExistentId_ShouldThrowNotFoundException() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Will Not Update");

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(999L, dto)
        );

        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }

    @Test
    void testDeleteUser_ShouldRemoveFromDatabase() {
        userService.deleteUser(testUserId);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(testUserId)
        );

        assertTrue(exception.getMessage().contains(testUserId.toString()));
    }

    @Test
    void testDeleteUser_NonExistentId_ShouldNotThrow() {
        assertDoesNotThrow(() -> userService.deleteUser(999L));
    }

    @Test
    void testGetUserById_ExistingId_ShouldReturnUser() {
        UserDto found = userService.getUserById(testUserId);

        assertEquals(testUserId, found.getId());
        assertEquals("Test User", found.getName());
        assertEquals("test@example.com", found.getEmail());
    }

    @Test
    void testGetUserById_NonExistentId_ShouldThrowNotFoundException() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        assertTrue(exception.getMessage().contains("doesn't exist"));
    }
}
