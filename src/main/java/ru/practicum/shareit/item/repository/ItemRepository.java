package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByUserId(Long userId);

    Item getItemById(Long itemId);

   // Item save(Item item, Long userId);

  //  Item updateItemById(Long itemId, Item item, Long userId);



    // Collection<Item> searchItemsByNameAndDescription(Long userId, String descr);

}
