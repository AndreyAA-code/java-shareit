package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.owner.id = :userId ORDER BY i.id")
    List<Item> findByOwnerId(@Param("userId") Long userId);

    @Query(value = "SELECT i FROM Item i WHERE i.available = true AND" +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) AND " +
            "i.owner.id = ?2")
    List<Item> searchItemsByNameAndDescription(String descr,Long userId);

    List<Item> findByOwner_Id(Long userId);

    List<Item> findByRequestId(Long id);
}
