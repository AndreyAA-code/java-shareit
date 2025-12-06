package ru.practicum.gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingItemDto item;
    private BookingUserDto booker;
    private BookingStatus status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingUserDto {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingItemDto {
        private Long id;
        private String name;
    }
}
