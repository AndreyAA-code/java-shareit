package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
        return itemRepository.findByUserId(userId)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("getItemById({})", itemId);
        return ItemMapper.mapItemToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public ItemDto createItem(ItemCreateDto itemCreateDto, Long userId) {
        userRepository.getUserById(userId);
        log.info("createItem({})", itemCreateDto);
        Item item = ItemMapper.mapItemDtoToItem(itemCreateDto);
        return ItemMapper.mapItemToDto(itemRepository.save(item, userId));
    }
/*
    @Override
    public ItemDto updateItemById(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        Item existingItem = itemRepository.getItemById(itemId);
        Item updatedItem = ItemMapper.mapItemUpdateDtoToItemFields(existingItem, itemUpdateDto);
        return ItemMapper.mapItemToDto(itemRepository.updateItemById(itemId, updatedItem, userId));
    }
/*
    @Override
    public Collection<ItemDto> searchItemsByNameAndDescription(String descr, Long userId) {
        return itemRepository.searchItemsByNameAndDescription(userId, descr)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    } */
}
