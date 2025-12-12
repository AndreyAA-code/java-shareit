package ru.practicum.server.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.server.request.model.ItemRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestor(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest mapToItemRequest(ItemRequestCreateDto itemRequestCreateDto) {
        return ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .build();
    }
}
