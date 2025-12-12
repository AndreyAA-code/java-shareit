package ru.practicum.server.integration_tests;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingState;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exceptions.NoRightsException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.exceptions.StatusException;
import ru.practicum.server.exceptions.UnavailableItemException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    private Long bookerId;
    private Long ownerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        User booker = User.builder()
                .name("Booker")
                .email("booker@example.com")
                .build();
        entityManager.persist(booker);
        entityManager.flush();
        this.bookerId = booker.getId();

        User owner = User.builder()
                .name("Owner")
                .email("owner@example.com")
                .build();
        entityManager.persist(owner);
        entityManager.flush();
        this.ownerId = owner.getId();

        Item item = Item.builder()
                .name("Test Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item);
        entityManager.flush();
        this.itemId = item.getId();
    }

    @Test
    void testCreateBooking_Success() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto result = bookingService.createBooking(dto, bookerId);

        assertThat(result).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(itemId);
        assertThat(result.getBooker().getId()).isEqualTo(bookerId);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getStart()).isEqualTo(dto.getStart());
        assertThat(result.getEnd()).isEqualTo(dto.getEnd());
    }

    @Test
    void testCreateBooking_ItemNotAvailable_ThrowsException() {
        Item item = entityManager.find(Item.class, itemId);

        item.setAvailable(false);
        entityManager.persist(item);
        entityManager.flush();

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, bookerId))
                .isInstanceOf(UnavailableItemException.class)
                .hasMessageContaining("Item is not available");
    }

    @Test
    void testApprove_Success_Approval() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        BookingDto result = bookingService.approve(ownerId, booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testApprove_NotOwner_ThrowsNoRightsException() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        assertThatThrownBy(() -> bookingService.approve(bookerId, booking.getId(), true))
                .isInstanceOf(NoRightsException.class)
                .hasMessageContaining("User doesn't have sufficient rights");
    }

    @Test
    void testFindBookingById_Success() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        BookingDto result = bookingService.findBookingById(booking.getId(), bookerId);

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getBooker().getId()).isEqualTo(bookerId);
    }

    @Test
    void testFindBookingById_NotBelongsToUser_ThrowsNotFoundException() {
        User stranger = User.builder()
                .name("Stranger")
                .email("stranger@example.com")
                .build();
        entityManager.persist(stranger);
        entityManager.flush();

        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        assertThatThrownBy(() -> bookingService.findBookingById(booking.getId(), stranger.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");
    }

    @Test
    void testFindAllBookings_Current() {
        Booking past = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        Booking current = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(past);
        entityManager.flush();
        entityManager.persist(current);
        entityManager.flush();

        List<BookingDto> result = bookingService.findAllBookings(bookerId, BookingState.CURRENT);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(current.getId());
    }

    @Test
    void testFindAllBookings_Waiting() {
        Booking waiting = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(waiting);
        entityManager.flush();

        List<BookingDto> result = bookingService.findAllBookings(bookerId, BookingState.WAITING);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testApproveBooking_OwnerApproves_Success() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        BookingDto result = bookingService.approve(ownerId, booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getItem().getId()).isEqualTo(itemId);
        assertThat(result.getBooker().getId()).isEqualTo(bookerId);
    }

    @Test
    void testApproveBooking_OwnerRejects_Success() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        BookingDto result = bookingService.approve(ownerId, booking.getId(), false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void testApproveBooking_NotOwner_ThrowsException() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        assertThatThrownBy(() ->
                bookingService.approve(bookerId, booking.getId(), true))
                .isInstanceOf(NoRightsException.class)
                .hasMessageContaining("User doesn't have sufficient rights");
    }

    @Test
    void testGetUserBookings_AllState_Success() {
        Booking b1 = createBooking(entityManager.find(User.class, bookerId), entityManager.find(Item.class, itemId), 1, 2, BookingStatus.APPROVED);
        Booking b2 = createBooking(entityManager.find(User.class, bookerId), entityManager.find(Item.class, itemId), 3, 4, BookingStatus.WAITING);
        entityManager.persist(b1);
        entityManager.flush();
        entityManager.persist(b2);
        entityManager.flush();

        List<BookingDto> result = bookingService.findAllBookings(bookerId, BookingState.ALL);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(b2.getId());
        assertThat(result.get(1).getId()).isEqualTo(b1.getId());
    }

    @Test
    void testGetUserBookings_OtherUser_ThrowsException() {
        Long nonExistentUserId = 999L;

        assertThatThrownBy(() ->
                bookingService.getByOwner(nonExistentUserId, BookingState.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Owner not found with id:");
    }

    @Test
    void testCreateBooking_InvalidDates_ThrowsException() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, bookerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("End time is before start time");
    }

    @Test
    void testCreateBooking_ItemAlreadyBooked_ThrowsException() {
        Booking existing = createBooking(
                entityManager.find(User.class, bookerId),
                entityManager.find(Item.class, itemId),
                1, 3, BookingStatus.APPROVED
        );
        entityManager.persist(existing);
        entityManager.flush();

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, bookerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Time conflict with existing bookings");
    }

    @Test
    void testCreateBooking_OwnerTriesToBookOwnItem_ThrowsException() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, ownerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Booker not allowed to book");
    }

    @Test
    void testApproveBooking_AlreadyApproved_ThrowsStatusException() {
        Booking booking = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        assertThatThrownBy(() -> bookingService.approve(ownerId, booking.getId(), true))
                .isInstanceOf(StatusException.class)
                .hasMessageContaining("status already set");
    }

    @Test
    void testGetByOwner_CurrentBookings_Success() {
        Booking past = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        Booking current = Booking.builder()
                .item(entityManager.find(Item.class, itemId))
                .booker(entityManager.find(User.class, bookerId))
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(past);
        entityManager.flush();
        entityManager.persist(current);
        entityManager.flush();

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingState.CURRENT);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(current.getId());
    }

    @Test
    void testGetByOwner_NoItems_ReturnsEmptyList() {
        User userWithNoItems = User.builder()
                .name("NoItemsUser")
                .email("noitems@example.com")
                .build();
        entityManager.persist(userWithNoItems);
        entityManager.flush();

        List<BookingDto> result = bookingService.getByOwner(userWithNoItems.getId(), BookingState.ALL);

        assertThat(result).isEmpty();
    }

    @Test
    void testFindBookingById_BookingNotFound_ThrowsNotFoundException() {
        assertThatThrownBy(() -> bookingService.findBookingById(999L, bookerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("booking not found");
    }


    private Booking createBooking(User booker, Item item, int startDays, int endDays, BookingStatus status) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(startDays))
                .end(LocalDateTime.now().plusDays(endDays))
                .status(status)
                .build();
    }

}

