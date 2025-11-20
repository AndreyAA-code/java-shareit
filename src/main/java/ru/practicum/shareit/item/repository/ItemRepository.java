package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Collection<Item> getItems(Long userId);

    Item getItemById(Long itemId);

    Item createItem(Item item, Long userId);

    Item updateItemById(Long itemId, Item item, Long userId);

    Collection<Item> searchItemsByNameAndDescription(String descr, Long userId);

}
