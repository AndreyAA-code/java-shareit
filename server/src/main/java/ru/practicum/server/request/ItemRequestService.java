package ru.practicum.server.request;

import ru.practicum.server.request.dto.ItemRequestCreateDto;
import ru.practicum.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long userId);

    List<ItemRequestDto> getOwnItemRequests(Long userId);

    List<ItemRequestDto> getAllItemRequests(Long userId);

    ItemRequestDto getItemRequest(Long requestId, Long userId);

}
