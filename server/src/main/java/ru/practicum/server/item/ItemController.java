package ru.practicum.server.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.item.dto.comment.CommentDto;
import ru.practicum.server.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.server.item.dto.item.ItemCreateDto;
import ru.practicum.server.item.dto.item.ItemDto;
import ru.practicum.server.item.dto.item.ItemUpdateDto;
import ru.practicum.server.item.service.ItemService;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping()
    public List<ItemCommentsLastNextBookingDto> getItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItems for user {}", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemCommentsLastNextBookingDto getItemById(@PathVariable Long itemId,
                                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItemById {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping()
    public ItemDto createItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("createItem()");
        return itemService.createItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("updateItemById()");
        return itemService.updateItemById(itemId, itemUpdateDto, userId);
    }

    @GetMapping ("/search")
    public Collection<ItemDto> searchAvailableItems(@RequestParam String text,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemService.searchItemsByNameAndDescription(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("addComment for item {} by user {}: {}", itemId, userId, commentDto.getText());
        return itemService.addComment(itemId,userId,commentDto);
    }

}
