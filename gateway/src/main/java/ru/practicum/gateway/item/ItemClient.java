package ru.practicum.gateway.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.booking.dto.BookingCreateDto;
import ru.practicum.gateway.booking.dto.BookingState;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.comment.CommentDto;
import ru.practicum.gateway.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.gateway.item.dto.item.ItemCreateDto;
import ru.practicum.gateway.item.dto.item.ItemDto;
import ru.practicum.gateway.item.dto.item.ItemUpdateDto;

import java.util.Collection;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String URL = "/items";

    @Value("${server.host}")
    private String host;

    @Autowired
    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public Collection<ItemCommentsLastNextBookingDto> getItems(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItems for user {}", userId);
        return ;
    }

    public ItemCommentsLastNextBookingDto getItemById(@PathVariable Long itemId,
                                                      @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("getItemById {}", itemId);
        return null;
    }

    public ItemDto createItem(@Valid @RequestBody ItemCreateDto itemCreateDto,
                              @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("createItem()");
        return null;
    }

    public ItemDto updateItemById(@PathVariable Long itemId,
                                  @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                  @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("updateItemById()");
        return null;
    }

    public Collection<ItemDto> searchAvailableItems(@RequestParam String text,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return null;
    }

    public CommentDto addComment(@PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        log.info("addComment for item {} by user {}: {}", itemId, userId, commentDto.getText());
        return null;
    }

}
