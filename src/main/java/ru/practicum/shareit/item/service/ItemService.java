package ru.practicum.shareit.item.service;


import jakarta.validation.Valid;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> getItems();

    ItemDto getItemById(Long itemId);

    ItemDto createItem(Item item);

    ItemDto updateItemById(Long itemId, Item item);
}
