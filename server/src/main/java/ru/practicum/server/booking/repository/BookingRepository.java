package ru.practicum.server.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findAllByItemInOrderByStartDesc(List<Item> itemIds);

    List<Booking> findAllByItemInAndEndIsAfterAndStartIsBeforeOrderByStartDesc(List<Item> itemId, LocalDateTime now, LocalDateTime now1);

    List<Booking> findAllByItemInAndEndIsBeforeOrderByStartDesc(List<Item> itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndStartIsAfterOrderByStartDesc(List<Item> itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndStatus(List<Item> itemId, BookingStatus bookingStatus);

    Boolean existsByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime now);

    /*
    @Query(value = "SELECT b FROM Booking b WHERE b.item.id = ?1 " +
            "AND b.status = ?2 AND ((b.start BETWEEN ?3 " +
            "AND ?4) OR (b.end BETWEEN ?3 AND ?4) " +
            "OR (b.start <= ?3 AND b.end >= ?4))")

    List<Booking> findByItemIdAndStatusAndTimeRange(Long itemId, BookingStatus status, LocalDateTime start, LocalDateTime end);
     */

    Optional<Booking> findTopByItem_IdAndEndAndStatusBeforeOrderByEndDesc(Long itemId, LocalDateTime now, BookingStatus bookingStatus);

    Optional<Booking> findTopByItem_IdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime now, BookingStatus bookingStatus);

    @Query(value = "SELECT COUNT(b) FROM Booking b WHERE b.item.id = ?1 " +
            "AND b.status = ?2 AND b.start < ?4 AND b.end > ?3")
    Long intersectionCount(Long itemId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId AND b.status = 'APPROVED' " +
            "ORDER BY b.start DESC")
    List<Booking> findApprovedBookingsByOwnerId(@Param("userId") Long userId);

}