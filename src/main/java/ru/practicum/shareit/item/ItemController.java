package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemCommentsDto;
import ru.practicum.shareit.item.dto.item.ItemCreateDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemUpdateDto;
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
    public ItemCommentsDto getItemById(@Valid @PathVariable Long itemId,
                                       @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId) {
        log.info("getItemById {}", itemId);
        return itemService.getItemById(itemId, userId);
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
    public CommentDto addComment(@Valid @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = "X-Sharer-User-Id", required = true) Long userId){
        log.info("!!!addComment{}",commentDto);
        return itemService.addComment(itemId,userId,commentDto);
    }

}
