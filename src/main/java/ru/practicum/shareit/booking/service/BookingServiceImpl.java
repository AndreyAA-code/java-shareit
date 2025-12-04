package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoRightsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StatusException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)

    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId())) {
            throw new NoRightsException("User doesn't have sufficient rights");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)
                || booking.getStatus().equals(BookingStatus.REJECTED)) {
            throw new StatusException("status already set");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.mapBookingToBookingDto(booking);
    }

    @Override
    public BookingDto createBooking(BookingCreateDto bookingCreateDto, Long userId) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingCreateDto.getItemId()));
        User booker = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Booker not found with id: " + userId));
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new RuntimeException("End time is before start time");
        }
        if (!item.getAvailable()) {
            throw new UnavailableItemException("Item is not available");
        }
        if (booker.equals(item.getOwner())) {
            throw new RuntimeException("Booker not allowed to book");
        }

        Booking booking = BookingMapper.mapBookingCreateDtoToBooking(bookingCreateDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        Long bookingsIntersectionCount = bookingRepository.intersectionCount(bookingCreateDto.getItemId(),
                BookingStatus.APPROVED, bookingCreateDto.getStart(), bookingCreateDto.getEnd());
       // List<Booking> bookings = bookingRepository.findByItemIdAndStatusAndTimeRange(bookingCreateDto.getItemId(),
        //        BookingStatus.APPROVED, bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        if (bookingsIntersectionCount >= 1) {
            throw new RuntimeException("Time conflict with existing bookings");
        }

        return BookingMapper.mapBookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        Item item = booking.getItem();
        if (!userId.equals(item.getOwner().getId()) && !userId.equals(booking.getBooker().getId())) {
            throw new NotFoundException("user not found");
        }
        return BookingMapper.mapBookingToBookingDto(booking);
    }

    @Override
    public List<BookingDto> findAllBookings(Long userId, BookingState bookingState) {
        User booker = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Booker not found with id: " + userId));
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::mapBookingToBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, BookingState bookingState) {
        User owner = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Owner not found with id: " + userId));
        List<Item> ownerItems = itemRepository.findByOwner_Id(userId);
        if (ownerItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = ownerItems
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Booking> bookings = new ArrayList<>();
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemInOrderByStartDesc(itemIds);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemInAndEndIsAfterAndStartIsBeforeOrderByStartDesc(itemIds, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemInAndEndIsBeforeOrderByStartDesc(itemIds, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemInAndStartIsAfterOrderByStartDesc(itemIds, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemInAndStatus(itemIds, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemInAndStatus(itemIds, BookingStatus.REJECTED);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::mapBookingToBookingDto)
                .collect(Collectors.toList());
    }

}
