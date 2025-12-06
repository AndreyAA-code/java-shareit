package ru.practicum.server.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {


    List<ItemRequest> findAllByRequestorId(Long userId);

    List<ItemRequest> findAllByRequestorIdNot(Long userId);

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long userId);

}
