package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndBookingStatus(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId, LocalDateTime now, LocalDateTime nowed);

    List<Booking> findAllByItemInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findAllByItemInAndEndIsAfterAndStartIsBeforeOrderByStartDesc(List<Long> itemId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemInAndEndIsBeforeOrderByStartDesc(List<Long> itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndStartIsAfterOrderByStartDesc(List<Long> itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndBookingStatus(List<Long> itemId, BookingStatus bookingStatus);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime now);

    Optional<Booking> findTopByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findTopByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);
}
