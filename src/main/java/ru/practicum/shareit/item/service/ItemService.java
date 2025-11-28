package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.shareit.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemCommentsLastNextBookingDto> getItems(Long userId);

    ItemCommentsLastNextBookingDto getItemById(Long itemId, Long userId);

    ItemDto createItem(ItemCreateDto itemCreateDto, Long userId);

    ItemDto updateItemById(Long itemId, ItemUpdateDto itemUpdateDto, Long userId);

    Collection<ItemDto> searchItemsByNameAndDescription(String descr, Long userId);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);
}
