package ru.practicum.server;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.item.dto.item.ItemCreateDto;
import ru.practicum.server.item.dto.item.ItemDto;
import ru.practicum.server.item.dto.item.ItemForItemRequestsDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.request.ItemRequestService;
import ru.practicum.server.request.dto.ItemRequestCreateDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private Long requesterId;
    private Long ownerId;
    private Long otherUserId;

    @BeforeEach
    void setUp() {
        UserCreateDto requesterDto = new UserCreateDto();
        requesterDto.setName("Requester");
        requesterDto.setEmail("requester@test.com");
        UserDto savedRequester = userService.createUser(requesterDto);
        requesterId = savedRequester.getId();

        UserCreateDto ownerDto = new UserCreateDto();
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner@test.com");
        UserDto savedOwner = userService.createUser(ownerDto);
        ownerId = savedOwner.getId();

        UserCreateDto otherUserDto = new UserCreateDto();
        otherUserDto.setName("Other User");
        otherUserDto.setEmail("other@test.com");
        UserDto savedOther = userService.createUser(otherUserDto);
        otherUserId = savedOther.getId();
    }

    @AfterEach
    void tearDown() {
        if (requesterId != null) userService.deleteUser(requesterId);
        if (ownerId != null) userService.deleteUser(ownerId);
        if (otherUserId != null) userService.deleteUser(otherUserId);
    }

    @Test
    void shouldCreateItemRequest_Successfully() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Нужна дрель на выходных");

        ItemRequestDto created = itemRequestService.create(requestDto, requesterId);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Нужна дрель на выходных", created.getDescription());
        assertNotNull(created.getCreated());
        assertTrue(created.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldGetOwnRequests_IncludesItems() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Ищу перфоратор");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, requesterId);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Перфоратор");
        itemDto.setDescription("Мощный перфоратор");
        itemDto.setAvailable(true);
        itemDto.setRequestId(createdRequest.getId());

        ItemDto savedItem = itemService.createItem(itemDto, ownerId);

        List<ItemRequestDto> requests = itemRequestService.getOwnItemRequests(requesterId);

        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        ItemRequestDto found = requests.get(0);

        assertEquals(createdRequest.getId(), found.getId());
        assertEquals("Ищу перфоратор", found.getDescription());
        assertEquals(1, found.getItems().size());

        ItemForItemRequestsDto item = found.getItems().get(0);
        assertEquals("Перфоратор", item.getName());
        assertEquals(ownerId, item.getOwnerId());
    }

    @Test
    void shouldGetAllRequests_ExcludesOwn() {
        ItemRequestCreateDto requestDto1 = new ItemRequestCreateDto();
        requestDto1.setDescription("Нужен шуруповерт");
        itemRequestService.create(requestDto1, requesterId);

        ItemRequestCreateDto requestDto2 = new ItemRequestCreateDto();
        requestDto2.setDescription("Ищу уровень");
        itemRequestService.create(requestDto2, otherUserId);

        List<ItemRequestDto> requests = itemRequestService.getAllItemRequests(requesterId);

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals("Ищу уровень", requests.get(0).getDescription());
    }

    @Test
    void shouldGetItemRequestById_ReturnsWithItems() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Мне нужна лестница");
        ItemRequestDto created = itemRequestService.create(requestDto, requesterId);

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Лестница");
        itemDto.setDescription("3 метра");
        itemDto.setAvailable(true);
        itemDto.setRequestId(created.getId());
        itemService.createItem(itemDto, ownerId);

        ItemRequestDto found = itemRequestService.getItemRequest(created.getId(), otherUserId); // любой пользователь может посмотреть

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Мне нужна лестница", found.getDescription());
        assertEquals(1, found.getItems().size());
        assertEquals("Лестница", found.getItems().get(0).getName());
    }

    @Test
    void shouldThrowNotFound_WhenRequestDoesNotExist() {
        Long nonExistentId = 999L;

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getItemRequest(nonExistentId, requesterId)
        );

        assertTrue(exception.getMessage().contains("Item with id " + nonExistentId + "not found"));
    }
}
