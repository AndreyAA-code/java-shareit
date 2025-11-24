package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto save(BookingCreateDto bookingCreateDto, Long userId) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + bookingCreateDto.getItemId()));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Booking booking = BookingMapper.mapBookingDtoToBooking(bookingCreateDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setBookingStatus(BookingStatus.WAITING);
        return BookingMapper.mapBookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long bookingId) {
        return BookingMapper.mapBookingToBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + bookingId + "doesn't exist")));
    }
}
