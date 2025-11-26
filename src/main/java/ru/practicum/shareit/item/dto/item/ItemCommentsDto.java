package ru.practicum.shareit.item.dto.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemCommentsDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean available;
    private String request;
    private List<CommentDto> comments;
}
