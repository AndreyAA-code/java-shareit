package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        log.info("getItems()");
        return itemRepository.findByOwner_Id(userId)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("getItemById({})", itemId);
        return ItemMapper.mapItemToDto(itemRepository.getItemById(itemId)
        .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + "doesn't exist")));
    }

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto, Long userId) {
        User owner = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + "doesn't exist"));
        log.info("createItem({})", itemCreateDto);
        Item item = ItemMapper.mapItemDtoToItem(itemCreateDto);
        item.setOwner(owner);
        return ItemMapper.mapItemToDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItemById(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        Item existingItem = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + "doesn't exist"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нет прав на просмотр. Владелец вещи в запросе не соответствует реальному");
        }
        log.info("updateItemById({}, {})", itemId, itemUpdateDto);
        Item updatedItem = ItemMapper.mapItemUpdateDtoToItemFields(existingItem, itemUpdateDto);
        log.info("updateItemById({}, {})", itemId, itemUpdateDto);
        updatedItem.setOwner(existingItem.getOwner());
        log.info("updateItemById({}, {})", itemId, itemUpdateDto);
        updatedItem.setId(itemId);
        log.info("updateItemById({}, {})", itemId, itemUpdateDto);
        return ItemMapper.mapItemToDto(itemRepository.save(updatedItem));
    }

    @Override
    public Collection<ItemDto> searchItemsByNameAndDescription(String descr, Long userId) {
        return itemRepository.searchItemsByNameAndDescription(descr, userId)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }
}
