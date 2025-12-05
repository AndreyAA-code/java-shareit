package ru.practicum.shareit.item.dto.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapItemToDto(Item item) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getOwner().getId())
                .available(item.getAvailable());
        if (item.getRequest() != null) {
            builder.request(item.getRequest().getId());
        }

        return builder.build();
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

    public static ItemCommentsLastNextBookingDto mapItemToItemCommentsDto(
            Item item, List<CommentDto> commentDtos, BookingDto lastBooking, BookingDto nextBooking) {
        return ItemCommentsLastNextBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .comments(commentDtos)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

}
