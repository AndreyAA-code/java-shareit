package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserId(Long userId);

    Optional<Item> getItemById(Long itemId);

    Item save(Item item);

  //  Item updateItemById(Long itemId, Item item, Long userId);

    // Collection<Item> searchItemsByNameAndDescription(Long userId, String descr);

}
