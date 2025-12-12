package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.comment.CommentDto;
import ru.practicum.server.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.server.item.dto.item.ItemCreateDto;
import ru.practicum.server.item.dto.item.ItemDto;
import ru.practicum.server.item.dto.item.ItemUpdateDto;
import java.util.List;

public interface ItemService {

    List<ItemCommentsLastNextBookingDto> getItems(Long userId);

    ItemCommentsLastNextBookingDto getItemById(Long itemId, Long userId);

    ItemDto createItem(ItemCreateDto itemCreateDto, Long userId);

    ItemDto updateItemById(Long itemId, ItemUpdateDto itemUpdateDto, Long userId);

    List<ItemDto> searchItemsByNameAndDescription(String descr, Long userId);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
