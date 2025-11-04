package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;

@Repository
public interface ItemRepository {

    Collection<Item> getItems(Long userId);

    Item getItemById(Long itemId);

    Item createItem(Item item, Long userId);

    Item updateItemById(Long itemId, Map <String, Object> updates, Long userId);
}
