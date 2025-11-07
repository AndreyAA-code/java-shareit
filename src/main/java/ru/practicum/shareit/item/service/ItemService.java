package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> getItems(Long userId);

    ItemDto getItemById(Long itemId);

    ItemDto createItem(ItemCreateDto itemCreateDto, Long userId);

    ItemDto updateItemById(Long itemId, ItemUpdateDto itemUpdateDto, Long userId);

    Collection<ItemDto> searchItemsByNameAndDescription(String descr, Long userId);

}
