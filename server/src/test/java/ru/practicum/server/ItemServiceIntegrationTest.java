package ru.practicum.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exceptions.NotAcceptableException;
import ru.practicum.server.item.dto.comment.CommentDto;
import ru.practicum.server.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.server.item.dto.item.ItemCreateDto;
import ru.practicum.server.item.dto.item.ItemDto;
import ru.practicum.server.item.dto.item.ItemUpdateDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

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
    private UserService userService;

    @Autowired
    BookingService bookingService;

    private Long ownerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setName("Test Owner");
        userDto.setEmail("owner@test.com");
        UserDto savedUser = userService.createUser(userDto);
        ownerId = savedUser.getId();

        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("This is a test item");
        itemDto.setAvailable(true);

        ItemDto savedItem = itemService.createItem(itemDto, ownerId);
        itemId = savedItem.getId();
    }

    @AfterEach
    void tearDown() {
        if (ownerId != null) {
            userService.deleteUser(ownerId);
            ownerId = null;
        }
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
        ItemCommentsLastNextBookingDto item = items.get(0);
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
        UserCreateDto bookerDto = new UserCreateDto();
        bookerDto.setName("Commenter");
        bookerDto.setEmail("commenter@test.com");
        UserDto savedBooker = userService.createUser(bookerDto);
        Long bookerId = savedBooker.getId();

        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto bookingDto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(now.minusHours(3))
                .end(now.minusHours(1))
                .build();

        BookingDto createdBooking = bookingService.createBooking(bookingDto, bookerId);
        BookingDto approvedBooking = bookingService.approve(createdBooking.getId(), ownerId, true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());

        CommentDto commentDto = CommentDto.builder()
                .text("Хорошая вещь")
                .build();

        CommentDto addedComment = itemService.addComment(itemId, bookerId, commentDto);

        assertNotNull(addedComment);
        assertEquals("Хорошая вещь", addedComment.getText());
        assertEquals("Commenter", addedComment.getAuthorName());
        assertNotNull(addedComment.getCreated());
    }

    @Test
    void shouldNotAddComment_WhenNoFinishedBooking() {
        UserCreateDto booker = new UserCreateDto();
        booker.setName("Commenter");
        booker.setEmail("commenter@test.com");
        UserDto savedBooker = userService.createUser(booker);
        Long bookerId = savedBooker.getId();

        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto booking = BookingCreateDto.builder()
                .itemId(itemId)
                .start(now.minusHours(3))
                .end(now.plusHours(1))
                .build();

        BookingDto createdBooking = bookingService.createBooking(booking, bookerId);
        BookingDto approvedBooking = bookingService.approve(createdBooking.getId(), ownerId, true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());

        CommentDto comment = CommentDto.builder()
                .text("Хочу оставить комментарий до окончания бронирования")
                .build();

        NotAcceptableException exception = assertThrows(
                NotAcceptableException.class,
                () -> itemService.addComment(itemId, bookerId, comment)
        );

        assertTrue(exception.getMessage().contains("has no completed booking for item"));
    }

}
