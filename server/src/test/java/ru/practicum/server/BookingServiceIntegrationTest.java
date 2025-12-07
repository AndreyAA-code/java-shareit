package ru.practicum.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exceptions.NoRightsException;
import ru.practicum.server.exceptions.UnavailableItemException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
}

