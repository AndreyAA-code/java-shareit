package ru.practicum.shareit.item.dto;

import org.hibernate.Hibernate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class CommentMapper {

    public static CommentDto mapCommentToCommentDto(Comment comment) {
        if (comment == null) return null;

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(mapToCommentItemDto(comment.getItem()))
                .author(mapToCommentUserDto(comment.getAuthor()))
                .created(comment.getCreated())
                .build();
    }

    private static CommentUserDto mapToCommentUserDto(User user) {
        if (user == null) return null;

        return CommentUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    private static CommentItemDto mapToCommentItemDto(Item item) {
        if (item == null) return null;

        if (item.getOwner() != null && !Hibernate.isInitialized(item.getOwner())) {
            Hibernate.initialize(item.getOwner());
        }

        return CommentItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(mapToCommentUserDto(item.getOwner()))
                .build();
    }

    public static Comment mapCommentDtoToComment(CommentDto commentDto, User author, Item item) {
        if (commentDto == null) return null;

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }
}
