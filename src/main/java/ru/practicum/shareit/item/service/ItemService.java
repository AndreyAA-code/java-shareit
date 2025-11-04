package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;

public interface ItemService {

    Collection<ItemDto> getItems(Long userId);

    ItemDto getItemById(Long itemId);

    ItemDto createItem(Item item, Long userId);

    ItemDto updateItemById(Long itemId, Map <String, Object> updates, Long userId);
}
