package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name ="bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="start_date")
    private Timestamp start;

    @Column(name="start_end")
    private Timestamp end;

    @Column(name ="item_id")
    private Long item;

    @Column(name = "booker_id")
    private Long booker;

    @Column(name ="status")
    private BookingStatus bookingStatus;
}