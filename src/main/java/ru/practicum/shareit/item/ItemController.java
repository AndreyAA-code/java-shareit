package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    ItemService itemService;

    @GetMapping()
    public Collection<ItemDto> getItems(@RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        log.info("getItems for user {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@Valid @PathVariable Long itemId) {
        log.info("getItemById {}", itemId);
        return itemService.getItemById(itemId);
    }

    @PostMapping()
    public ItemDto createItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                              @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        log.info("createItem()");
        return itemService.createItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@Valid @PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                  @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        log.info("updateItemById()");
        return itemService.updateItemById(itemId, itemUpdateDto, userId);
    }

    @GetMapping ("/search")
    public Collection<ItemDto> searchAvailableItems(@RequestParam String text,
                                                    @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        return itemService.searchItemsByNameAndDescription(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @PathVariable Long itemId, Comment comment,
                                 @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId){
        log.info("addComment()");
        return itemService.addComment(itemId,userId,comment);
    }
}
