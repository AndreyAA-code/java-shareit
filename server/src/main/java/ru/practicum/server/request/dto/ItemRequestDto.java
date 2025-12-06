package ru.practicum.server.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.server.item.dto.item.ItemForItemRequestsDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemForItemRequestsDto> items;
}
