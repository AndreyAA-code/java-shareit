package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        log.info("getItems()");
        return itemRepository.getItems(userId)
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
    public ItemDto createItem(Item item, Long userId) {
        userRepository.getUserById(userId);
        log.info("createItem({})", item);
        return ItemMapper.mapItemToDto(itemRepository.createItem(item, userId));
    }

    @Override
    public ItemDto updateItemById(Long itemId, Map<String, Object> updates, Long userId) {
        userRepository.getUserById(userId);
        log.info("updateItemById({})", itemId);
        return ItemMapper.mapItemToDto(itemRepository.updateItemById(itemId, updates, userId));
    }

    @Override
    public Collection<ItemDto> searchItemsByNameAndDescription(String descr, Long userId) {
        return itemRepository.searchItemsByNameAndDescription(descr, userId)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }
}
