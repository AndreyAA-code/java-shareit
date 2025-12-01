package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long userId);

    @Query(value = "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.comments c " +
            "LEFT JOIN FETCH i.bookings b " +
            "WHERE i.owner.id = ?1 " +
            "AND i.available = true " +
            "ORDER BY i.id")
        List<Item> getItems(Long userId);

    @Query(value = "SELECT i FROM Item i WHERE i.available = true AND" +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) AND " +
            "i.owner.id = ?2")
    List<Item> searchItemsByNameAndDescription(String descr,Long userId);

}
