package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto mapCommentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .author(comment.getAuthor())
                .created(comment.getCreated())
                .build();
    }
    public static Comment mapCommentDtoToComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(commentDto.getItem())
                .author(commentDto.getAuthor())
                .created(commentDto.getCreated())
                .build();
    }
}

