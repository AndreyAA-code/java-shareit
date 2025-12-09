package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.comment.CommentDto;
import ru.practicum.gateway.item.dto.item.ItemCreateDto;
import ru.practicum.gateway.item.dto.item.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {

    private static final String URL = "/items";

    @Value("${server.port}")
    private String host;

    @Autowired
    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> getItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return get(host + URL, userId);
    }

    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return get(host + URL + "/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return post(host + URL, userId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItemById(@PathVariable Long itemId,
                                                 @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return patch(host + URL + "/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> searchAvailableItems(@RequestParam String text,
                                                       @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return get(host + URL + "?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                              @Valid @RequestBody CommentDto commentDto,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return post(host + URL + "/" + itemId + "/comment", userId, commentDto);
    }

}
