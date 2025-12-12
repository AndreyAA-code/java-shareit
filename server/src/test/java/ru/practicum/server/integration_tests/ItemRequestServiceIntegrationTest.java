package ru.practicum.server.integration_tests;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.item.dto.item.ItemForItemRequestsDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.request.ItemRequestService;
import ru.practicum.server.request.dto.ItemRequestCreateDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    EntityManager entityManager;

    private Long requesterId;
    private Long ownerId;
    private Long otherUserId;

    @BeforeEach
    void setUp() {
        User requester = User.builder()
                .name("Requester")
                .email("requester@test.com")
                .build();
        entityManager.persist(requester);
        entityManager.flush();
        this.requesterId = requester.getId();

        User owner = User.builder()
                .name("Owner")
                .email("owner@test.com")
                .build();
        entityManager.persist(owner);
        entityManager.flush();
        this.ownerId = owner.getId();

        User otherUser = User.builder()
                .name("Other User")
                .email("other@test.com")
                .build();
        entityManager.persist(otherUser);
        entityManager.flush();
        this.otherUserId = otherUser.getId();
    }

    @Test
    void shouldCreateItemRequest_Successfully() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Нужна дрель");

        ItemRequestDto created = itemRequestService.create(requestDto, requesterId);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Нужна дрель", created.getDescription());
        assertNotNull(created.getCreated());
        assertTrue(created.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void shouldGetOwnRequests_IncludesItems() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Ищу перфоратор");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, requesterId);

        Item item = Item.builder()
                .name("Перфоратор")
                .description("Мощный перфоратор")
                .available(true)
                .owner(entityManager.getReference(User.class, ownerId))
                .request(entityManager.getReference(ItemRequest.class, createdRequest.getId()))
                .build();

        entityManager.persist(item);
        entityManager.flush();

        List<ItemRequestDto> requests = itemRequestService.getOwnItemRequests(requesterId);
        assertNotNull(requests);
        assertFalse(requests.isEmpty());
        ItemRequestDto found = requests.getFirst();

        assertEquals(createdRequest.getId(), found.getId());
        assertEquals("Ищу перфоратор", found.getDescription());
        assertEquals(1, found.getItems().size());

        ItemForItemRequestsDto itemDto = found.getItems().getFirst();
        assertEquals("Перфоратор", itemDto.getName());
        assertEquals(ownerId, itemDto.getOwnerId());
        assertEquals(ownerId, itemDto.getOwnerId());
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
        assertEquals("Ищу уровень", requests.getFirst().getDescription());
    }

    @Test
    void shouldGetItemRequestById_ReturnsWithItems() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto();
        requestDto.setDescription("Мне нужна лестница");
        ItemRequestDto created = itemRequestService.create(requestDto, requesterId);

        Item item = Item.builder()
                .name("Лестница")
                .description("3 метра")
                .available(true)
                .owner(entityManager.getReference(User.class, ownerId))
                .request(entityManager.getReference(ItemRequest.class, created.getId()))
                .build();

        entityManager.persist(item);
        entityManager.flush();

        ItemRequestDto found = itemRequestService.getItemRequest(created.getId(), otherUserId);

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Мне нужна лестница", found.getDescription());
        assertEquals(1, found.getItems().size());
        assertEquals("Лестница", found.getItems().getFirst().getName());

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