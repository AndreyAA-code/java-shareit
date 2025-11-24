package ru.practicum.shareit.booking.service;

import jakarta.servlet.UnavailableException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(BookingCreateDto bookingCreateDto, Long userId) {
        Item item = itemRepository.getItemById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + bookingCreateDto.getItemId()));
        User booker = userRepository.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("Booker not found with id: " + userId));
        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new RuntimeException("End time is before start time");
        }
        if (!item.getAvailable()){
            throw new UnavailableItemException("Item is not available");
        }
        if (booker.equals(item.getUser())) {
            throw new RuntimeException("Booker not allowed to book");
        }
        Booking booking = BookingMapper.mapBookingCreateDtoToBooking(bookingCreateDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setBookingStatus(BookingStatus.WAITING);
        return BookingMapper.mapBookingToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBookingById(Long bookingId, Long userId) {
        return BookingMapper.mapBookingToBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId)));
    }

    @Override
    public List<BookingDto> findAllBookings() {
        return List.of();
    }

}
