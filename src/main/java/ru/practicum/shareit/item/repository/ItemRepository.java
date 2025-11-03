package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Repository
public interface ItemRepository {

    Collection<Item> getItems();

    Item getItemById(Long itemId);

    Item createItem(Item item);

    Item updateItemById(Long itemId, Item item);
}
