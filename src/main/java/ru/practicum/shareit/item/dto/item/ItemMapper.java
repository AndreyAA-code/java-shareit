package ru.practicum.shareit.item.dto.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapItemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static Item mapItemDtoToItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
                .available(itemCreateDto.getAvailable())
                .build();
    }

    public static Item mapItemUpdateDtoToItemFields(Item item, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto.hasName()) {
            item.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.hasDescription()) {
            item.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.hasAvailable()) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }
        return item;
    }

    public static ItemCommentsDto mapItemToItemCommentsDto(Item item, List<CommentDto> commentDtos) {
        return ItemCommentsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .comments(commentDtos)
                .build();
    }

}
