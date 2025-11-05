package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    Long id;
    String description;
    Long requestor;
    Timestamp request;
}
