package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Override
    public Collection<ItemDto> getItems() {
        return List.of();
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return null;
    }

    @Override
    public ItemDto createItem(Item item) {
        return null;
    }

    @Override
    public ItemDto updateItemById(Long itemId) {
        return null;
    }
}
