package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> getItems(Long userId) {
        log.info("getItems by userId = {}", userId);
        return items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        log.info("getItemById({})", itemId);
        return items.get(itemId);
    }

    @Override
    public Item createItem(Item item, Long userId) {
        log.info("createItem({})", item);
        item.setId(getNextId());
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        log.info("created Item({})", item);
        return item;
    }

    @Override
    public Item updateItemById(Long itemId, Item item, Long userId) {
        checkItemById(itemId);
        if (!items.get(itemId).getOwnerId().equals(userId)) {
            throw new NotFoundException("Нет прав на просмотр. Владелец вещи в запросе не соответствует реальному");
        }
        return items.put(itemId, item);
    }

    @Override
    public Collection<Item> searchItemsByNameAndDescription(String descr, Long userId) {
        log.info("searchItemsByNameAndDescription({})", descr);
        if (descr == null || descr.isEmpty()) {
            return List.of();
        }
        String searchText = descr.toLowerCase();
       return items.values().stream()
               .filter(item -> item.getAvailable() == true
               || !item.getOwnerId().equals(userId))
                .filter(item -> item.getDescription().toLowerCase().contains(searchText)
                        || item.getName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }

    private Long getNextId() {
        log.info("getNextId()");
        Long maxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L);
        log.info("maxId = {}.", maxId);
        return ++maxId;
    }

    private void checkItemById(Long itemId) {
        log.info("checkItemById({})", itemId);
        if (!(items.containsKey(itemId))) {
            throw new NotFoundException("Item with id:" + itemId + " not found");
        }
    }


}
