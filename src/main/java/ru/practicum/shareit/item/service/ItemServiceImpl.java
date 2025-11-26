package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotAcceptableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapper;
import ru.practicum.shareit.item.dto.item.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Collection<ItemDto> getItems(Long userId) {
        log.info("getItems()");
        return itemRepository.findByOwner_Id(userId)
                .stream()
                .map(ItemMapper::mapItemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemCommentsDto getItemById(Long itemId, Long userId) {
        log.info("getItemById({}) by user {}", itemId, userId);

        // Get the item
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " doesn't exist"));

        // Get comments for the item
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::mapCommentToCommentDto)
                .collect(Collectors.toList());

        // Create and return ItemCommentsDto
        return ItemMapper.mapItemToItemCommentsDto(item, commentDtos);
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

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User author = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + "doesn't exist"));
        Item item = itemRepository.getItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + "doesn't exist"));
        Boolean isBookingExistsAndFinished = bookingRepository.existsByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now());
        if (!isBookingExistsAndFinished) {
            throw new NotAcceptableException("User with id: " + userId + " has no completed booking for item " + itemId);
        }
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        comment.setText(commentDto.getText());

        return CommentMapper.mapCommentToCommentDto(commentRepository.save(comment));
    }
}
