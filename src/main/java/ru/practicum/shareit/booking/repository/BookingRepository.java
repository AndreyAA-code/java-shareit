package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import ru.practicum.shareit.booking.model.Booking;

public class BookingRepository extends JpaRepository<Booking, Long> {

}
