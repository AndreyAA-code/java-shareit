package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemCommentsLastNextBookingDto> getItems(Long userId) {

        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> comments = commentRepository.findByItemIdsIn(itemIds);

        List<Booking> bookings = bookingRepository.findApprovedBookingsByOwnerId(userId);

        Map<Long, List<Comment>> commentsByItem = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        Map<Long, List<Booking>> bookingsByItem = bookings.stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        return items.stream().map(item -> {
            List<Comment> itemComments = commentsByItem.getOrDefault(item.getId(), Collections.emptyList());
            List<Booking> itemBookings = bookingsByItem.getOrDefault(item.getId(), Collections.emptyList());

            Booking lastBooking = itemBookings.stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            Booking nextBooking = itemBookings.stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            return ItemMapper.mapItemToItemCommentsDto(
                    item,
                    itemComments.stream().map(CommentMapper::mapCommentToCommentDto).collect(Collectors.toList()),
                    lastBooking != null ? BookingMapper.mapBookingToBookingDto(lastBooking) : null,
                    nextBooking != null ? BookingMapper.mapBookingToBookingDto(nextBooking) : null

            );
        }).collect(Collectors.toList());
    }

    @Override
    public ItemCommentsLastNextBookingDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + " doesn't exist"));
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::mapCommentToCommentDto)
                .collect(Collectors.toList());

        BookingDto lastBookingDto = null;
        BookingDto nextBookingDto = null;

        if (item.getOwner().getId().equals(userId)) {
            Optional<Booking> lastBooking = bookingRepository.findTopByItem_IdAndEndAndStatusBeforeOrderByEndDesc(
                    itemId, LocalDateTime.now(), BookingStatus.APPROVED);
            lastBookingDto = lastBooking.map(BookingMapper::mapBookingToBookingDto).orElse(null);

            Optional<Booking> nextBooking = bookingRepository.findTopByItem_IdAndStartAfterAndStatusOrderByStartAsc(
                    itemId, LocalDateTime.now(), BookingStatus.APPROVED);
            nextBookingDto = nextBooking.map(BookingMapper::mapBookingToBookingDto).orElse(null);
        }
        return ItemMapper.mapItemToItemCommentsDto(item, commentDtos, lastBookingDto, nextBookingDto);
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
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + "doesn't exist"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нет прав на просмотр. Владелец вещи в запросе не соответствует реальному");
        }
        log.info("updateItemById({}, {})", itemId, itemUpdateDto);
        Item updatedItem = ItemMapper.mapItemUpdateDtoToItemFields(existingItem, itemUpdateDto);
        updatedItem.setOwner(existingItem.getOwner());
        updatedItem.setId(itemId);
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id: " + itemId + "doesn't exist"));
        Boolean isBookingExistsAndFinished = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
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
