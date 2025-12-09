package ru.practicum.server.controler_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.booking.BookingController;
import ru.practicum.server.booking.dto.BookingCreateDto;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.model.BookingState;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.service.BookingService;
import ru.practicum.server.exceptions.NotFoundException;
import static org.hamcrest.Matchers.hasSize;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long bookingId = 100L;
    private final Long itemId = 10L;

    @Test
    void shouldCreateBooking_Success() throws Exception {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setStart(LocalDateTime.now().plusDays(1));
        createDto.setEnd(LocalDateTime.now().plusDays(2));
        createDto.setItemId(itemId);

        BookingDto savedDto = BookingDto.builder()
                .id(bookingId)
                .start(createDto.getStart())
                .end(createDto.getEnd())
                .item(new BookingDto.BookingItemDto(itemId, "Item Name"))
                .booker(new BookingDto.BookingUserDto(userId, "User Name"))
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.createBooking(any(BookingCreateDto.class), eq(userId)))
                .thenReturn(savedDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(userId));


        verify(bookingService, times(1)).createBooking(any(BookingCreateDto.class), eq(userId));
    }

    @Test
    void shouldReturnBadRequest_WhenStartIsPast() throws Exception {
        BookingCreateDto invalidDto = new BookingCreateDto();
        invalidDto.setStart(LocalDateTime.now().minusDays(1));
        invalidDto.setEnd(LocalDateTime.now().plusDays(1));
        invalidDto.setItemId(itemId);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any(), eq(userId));
    }

    @Test
    void shouldReturnBadRequest_WhenItemIdIsNull() throws Exception {
        BookingCreateDto invalidDto = new BookingCreateDto();
        invalidDto.setStart(LocalDateTime.now().plusDays(1));
        invalidDto.setEnd(LocalDateTime.now().plusDays(2));
        invalidDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any(), eq(userId));
    }

    @Test
    void shouldApproveBooking_Success() throws Exception {
        BookingDto approvedDto = BookingDto.builder()
                .id(bookingId)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.approve(eq(userId), eq(bookingId), eq(true)))
                .thenReturn(approvedDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).approve(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    void shouldRejectBooking_Success() throws Exception {
        BookingDto rejectedDto = BookingDto.builder()
                .id(bookingId)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingService.approve(eq(userId), eq(bookingId), eq(false)))
                .thenReturn(rejectedDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        verify(bookingService, times(1)).approve(eq(userId), eq(bookingId), eq(false));
    }

    @Test
    void shouldReturnNotFound_WhenBookingDoesNotExist_OnApprove() throws Exception {
        when(bookingService.approve(eq(userId), eq(999L), eq(true)))
                .thenThrow(new NotFoundException("Booking not found"));


        mockMvc.perform(patch("/bookings/999", 999L)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approve(eq(userId), eq(999L), eq(true));
    }

    @Test
    void shouldReturnBadRequest_WhenStartIsNull() throws Exception {
        BookingCreateDto invalidDto = new BookingCreateDto();
        invalidDto.setEnd(LocalDateTime.now().plusDays(1));
        invalidDto.setItemId(itemId);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any(), eq(userId));
    }

    @Test
    void shouldReturnBadRequest_WhenEndIsNull() throws Exception {
        BookingCreateDto invalidDto = new BookingCreateDto();
        invalidDto.setStart(LocalDateTime.now().plusDays(1));
        invalidDto.setItemId(itemId);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any(), eq(userId));
    }

    @Test
    void shouldGetBookingById_Success() throws Exception {
        BookingDto expectedDto = BookingDto.builder()
                .id(bookingId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.findBookingById(eq(bookingId), eq(userId)))
                .thenReturn(expectedDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("WAITING"));


        verify(bookingService, times(1)).findBookingById(eq(bookingId), eq(userId));
    }

    @Test
    void shouldReturnNotFound_WhenBookingDoesNotExist_OnGetById() throws Exception {
        when(bookingService.findBookingById(eq(999L), eq(userId)))
                .thenThrow(new NotFoundException("Booking not found"));


        mockMvc.perform(get("/bookings/999", 999L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());


        verify(bookingService, times(1)).findBookingById(eq(999L), eq(userId));
    }

    @Test
    void shouldGetAllBookings_ByState() throws Exception {
        List<BookingDto> bookings = List.of(
                BookingDto.builder().id(101L).status(BookingStatus.APPROVED).build(),
                BookingDto.builder().id(102L).status(BookingStatus.REJECTED).build()
        );

        when(bookingService.findAllBookings(eq(userId), eq(BookingState.ALL)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].status").value("REJECTED"));


        verify(bookingService, times(1)).findAllBookings(eq(userId), eq(BookingState.ALL));
    }
}