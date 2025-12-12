package ru.practicum.gateway.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.comment.CommentDto;
import ru.practicum.gateway.item.dto.item.ItemCreateDto;
import ru.practicum.gateway.item.dto.item.ItemUpdateDto;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping()
    public ResponseEntity<Object> getItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItems for user {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object>  getItemById(@PathVariable Long itemId,
                                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItemById {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestBody ItemCreateDto itemCreateDto,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("createItem()");
        return itemClient.createItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object>  updateItemById(@PathVariable Long itemId,
                                  @RequestBody ItemUpdateDto itemUpdateDto,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("updateItemById()");
        return itemClient.updateItemById(itemId, itemUpdateDto, userId);
    }

    @GetMapping ("/search")
    public ResponseEntity<Object> searchAvailableItems(@RequestParam String text,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemClient.searchAvailableItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object>  addComment(@PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("addComment for item {} by user {}: {}", itemId, userId, commentDto.getText());
        return itemClient.addComment(itemId,commentDto, userId);
    }

}
