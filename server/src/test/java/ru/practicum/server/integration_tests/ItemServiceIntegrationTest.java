package ru.practicum.server.integration_tests;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.exceptions.NotAcceptableException;
import ru.practicum.server.item.dto.comment.CommentDto;
import ru.practicum.server.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.server.item.dto.item.ItemCreateDto;
import ru.practicum.server.item.dto.item.ItemDto;
import ru.practicum.server.item.dto.item.ItemUpdateDto;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    EntityManager entityManager;

    private Long ownerId;
    private Long itemId;
    private Long bookerId;

    @BeforeEach
    void setUp() {
            User owner = User.builder()
                    .name("Test Owner")
                    .email("owner@test.com")
                    .build();
            entityManager.persist(owner);
            entityManager.flush();
            ownerId = owner.getId();

            Item item = Item.builder()
                    .name("Test Item")
                    .description("This is a test item")
                    .available(true)
                    .owner(owner)
                    .build();
            entityManager.persist(item);
            entityManager.flush();
            itemId = item.getId();

            User booker = User.builder()
                    .name("Commenter")
                    .email("commenter@test.com")
                    .build();
            entityManager.persist(booker);
            entityManager.flush();
            bookerId = booker.getId();

    }

    @Test
    void shouldGetItemById_ReturnsItemWithDetails() {
        ItemCommentsLastNextBookingDto found = itemService.getItemById(itemId, ownerId);

        assertNotNull(found);
        assertEquals(itemId, found.getId());
        assertEquals("Test Item", found.getName());
        assertEquals("This is a test item", found.getDescription());
        assertTrue(found.getAvailable());
    }


    @Test
    void shouldGetItemsByOwnerId_ReturnsItemList() {
        List<ItemCommentsLastNextBookingDto> items = itemService.getItems(ownerId);

        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        ItemCommentsLastNextBookingDto item = items.getFirst();
        assertEquals("Test Item", item.getName());
    }

    @Test
    void shouldSearchItemsByNameOrDescription_ReturnsMatchingItems() {
        List<ItemDto> results = itemService.searchItemsByNameAndDescription("test", ownerId);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(item -> item.getName().contains("Test Item")));
    }

    @Test
    void shouldCreateItem_Successfully() {
        ItemCreateDto newItemDto = new ItemCreateDto();
        newItemDto.setName("New Tool");
        newItemDto.setDescription("A very useful tool");
        newItemDto.setAvailable(true);

        ItemDto created = itemService.createItem(newItemDto, ownerId);

        assertNotNull(created);
        assertEquals("New Tool", created.getName());
        assertEquals("A very useful tool", created.getDescription());
        assertTrue(created.getAvailable());
    }

    @Test
    void shouldUpdateItem_Successfully() {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Item");
        updateDto.setDescription("Updated description");
        updateDto.setAvailable(false);

        ItemDto updated = itemService.updateItemById(itemId, updateDto, ownerId);

        assertNotNull(updated);
        assertEquals("Updated Item", updated.getName());
        assertEquals("Updated description", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void shouldAddComment_WhenUserHasFinishedBooking() {
        Booking booking = Booking.builder()
                .item(entityManager.getReference(Item.class, itemId))
                .booker(entityManager.getReference(User.class, bookerId))
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().minusHours(1))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        CommentDto commentDto = CommentDto.builder()
                .text("Хорошая вещь")
                .build();

        CommentDto addedComment = itemService.addComment(itemId, bookerId, commentDto);

        assertNotNull(addedComment);
        assertEquals("Хорошая вещь", addedComment.getText());
        assertEquals("Commenter", addedComment.getAuthorName());
        assertNotNull(addedComment.getCreated());
        assertTrue(addedComment.getCreated().isBefore(LocalDateTime.now().plusSeconds(10)));
    }

    @Test
    @Transactional
    void shouldNotAddComment_WhenNoFinishedBooking() {
        Booking booking = Booking.builder()
                .item(entityManager.getReference(Item.class, itemId))
                .booker(entityManager.getReference(User.class, bookerId))
                .start(LocalDateTime.now().minusHours(3))
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        CommentDto commentDto = CommentDto.builder()
                .text("Хочу оставить комментарий до окончания бронирования")
                .build();

        NotAcceptableException exception = assertThrows(
                NotAcceptableException.class,
                () -> itemService.addComment(itemId, bookerId, commentDto)
        );

        assertTrue(
                exception.getMessage().contains("has no completed booking for item"),
                "Expected error message to indicate missing completed booking"
        );
    }

}
