package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@Primary
public class InMemoryItemRepository implements ItemRepository {

    Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> getItems() {
        log.info("getItems()");
        return items.values()
                .stream()
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
        if (items.get(itemId).getOwnerId() != userId) {
            throw new NotFoundException("Владелец вещи в запросе не соответствует реальному");
        }
        log.info("updateItemById({})", itemId);

        return null;
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
            throw new NotFoundException("Item with id:" + itemId +" not found");
        }
    }


}
