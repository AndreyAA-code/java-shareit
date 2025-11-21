package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapItemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .ownerId(item.getUser().getId())
                .available(item.getAvailable())
                .request(item.getRequest())
                .build();
    }

    public static Item mapItemDtoToItem(ItemCreateDto itemCreateDto) {
        return Item.builder()
                .name(itemCreateDto.getName())
                .description(itemCreateDto.getDescription())
              //  .user(itemCreateDto.getOwnerId())
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
}
