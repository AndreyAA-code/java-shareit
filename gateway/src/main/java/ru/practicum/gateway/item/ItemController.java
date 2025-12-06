package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.comment.CommentDto;
import ru.practicum.gateway.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.gateway.item.dto.item.ItemCreateDto;
import ru.practicum.gateway.item.dto.item.ItemDto;
import ru.practicum.gateway.item.dto.item.ItemUpdateDto;


import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @GetMapping()
    public Collection<ItemCommentsLastNextBookingDto> getItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItems for user {}", userId);
        return null;
    }

    @GetMapping("/{itemId}")
    public ItemCommentsLastNextBookingDto getItemById(@PathVariable Long itemId,
                                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItemById {}", itemId);
        return null;
    }

    @PostMapping()
    public ItemDto createItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("createItem()");
        return null;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("updateItemById()");
        return null;
    }

    @GetMapping ("/search")
    public Collection<ItemDto> searchAvailableItems(@RequestParam String text,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return null;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("addComment for item {} by user {}: {}", itemId, userId, commentDto.getText());
        return null;
    }

}
