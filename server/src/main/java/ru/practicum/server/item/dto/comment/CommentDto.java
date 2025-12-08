package ru.practicum.server.item.dto.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    @JsonIgnore
    private CommentItemDto item;
    private String authorName;
    @JsonIgnore
    private CommentUserDto author;
    LocalDateTime created;
}
