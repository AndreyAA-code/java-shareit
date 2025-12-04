package ru.practicum.shareit.item.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    @JsonIgnore
    private CommentItemDto item;
    private String authorName;
    @JsonIgnore
    private CommentUserDto author;
    LocalDateTime created;
}
