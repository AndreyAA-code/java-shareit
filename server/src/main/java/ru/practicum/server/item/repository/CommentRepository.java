package ru.practicum.server.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.server.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment save(Comment comment);

    List<Comment> findByItemId(Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.item.id IN :itemIds ORDER BY c.created DESC")
    List<Comment> findByItemIdsIn(@Param("itemIds") List<Long> itemIds);

}
