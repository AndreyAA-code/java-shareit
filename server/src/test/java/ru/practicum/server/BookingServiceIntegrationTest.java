package ru.practicum.server;

import org.junit.jupiter.api.AfterEach;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
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
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exceptions.NoRightsException;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.exceptions.UnavailableItemException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceIntegrationTest {

        @Autowired
        private BookingService bookingService;
        @Autowired
        private BookingRepository bookingRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private ItemRepository itemRepository;


    private User booker;
    private User owner;
    private Item item;

    @BeforeAll
    void setUp() {
        booker = User.builder()
                .id(1L)
                .name("Booker")
                .email("booker@example.com")
                .build();
        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        userRepository.save(booker);
        userRepository.save(owner);

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Description")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateBooking_Success() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto result = bookingService.createBooking(dto, booker.getId());

        assertThat(result).isNotNull();
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getStart()).isEqualTo(dto.getStart());
        assertThat(result.getEnd()).isEqualTo(dto.getEnd());
    }

    @Test
    void testCreateBooking_ItemNotAvailable_ThrowsException() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, booker.getId()))
                .isInstanceOf(UnavailableItemException.class)
                .hasMessageContaining("Item is not available");
    }

    @Test
    void testApprove_Success_Approval() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        BookingDto result = bookingService.approve(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void testApprove_NotOwner_ThrowsNoRightsException() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        assertThatThrownBy(() -> bookingService.approve(booker.getId(), booking.getId(), true))
                .isInstanceOf(NoRightsException.class)
                .hasMessageContaining("User doesn't have sufficient rights");
    }

    @Test
    void testFindBookingById_Success() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        BookingDto result = bookingService.findBookingById(booking.getId(), booker.getId());

        assertThat(result.getId()).isEqualTo(booking.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void testFindBookingById_NotBelongsToUser_ThrowsNotFoundException() {
        User stranger = User.builder()
                .id(3L)
                .name("Stranger")
                .email("stranger@example.com")
                .build();
        userRepository.save(stranger);

        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        assertThatThrownBy(() -> bookingService.findBookingById(booking.getId(), stranger.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("user not found");
    }

    @Test
    void testFindAllBookings_Current() {
        Booking past = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        Booking current = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.saveAll(List.of(past, current));


        List<BookingDto> result = bookingService.findAllBookings(booker.getId(), BookingState.CURRENT);


        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(current.getId());
    }

    @Test
    void testFindAllBookings_Waiting() {
        Booking waiting = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(waiting);

        List<BookingDto> result = bookingService.findAllBookings(booker.getId(), BookingState.WAITING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void testApproveBooking_OwnerApproves_Success() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        BookingDto result = bookingService.approve(owner.getId(), booking.getId(), true);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void testApproveBooking_OwnerRejects_Success() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        BookingDto result = bookingService.approve(owner.getId(), booking.getId(), false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void testApproveBooking_NotOwner_ThrowsException() {
        Booking booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);

        assertThatThrownBy(() ->
                bookingService.approve(booker.getId(), booking.getId(), true))
                .isInstanceOf(NoRightsException.class)
                .hasMessageContaining("User doesn't have sufficient rights");
    }

    @Test
    void testGetUserBookings_AllState_Success() {
        Booking b1 = createBooking(booker, item, 1, 2, BookingStatus.APPROVED);
        Booking b2 = createBooking(booker, item, 3, 4, BookingStatus.WAITING);
        bookingRepository.saveAll(List.of(b1, b2));


        List<BookingDto> result = bookingService.findAllBookings(booker.getId(), BookingState.ALL);


        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(b2.getId());
        assertThat(result.get(1).getId()).isEqualTo(b1.getId());
    }

    @Test
    void testGetUserBookings_OtherUser_ThrowsException() {
        User otherUser = User.builder().id(999L).build();

        assertThatThrownBy(() ->
                bookingService.getByOwner(otherUser.getId(), BookingState.ALL))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Owner not found with id:");
    }

    @Test
    void testCreateBooking_InvalidDates_ThrowsException() {
        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, booker.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("End time is before start time");
    }

    @Test
    void testCreateBooking_ItemAlreadyBooked_ThrowsException() {
        Booking booking = createBooking(booker, item, 1, 3, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        BookingCreateDto dto = BookingCreateDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(dto, booker.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Time conflict with existing bookings");
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

