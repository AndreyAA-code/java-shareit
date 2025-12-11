package ru.practicum.server.item.dto.item;

import lombok.Data;

@Data
public class ItemCreateDto {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
