package ru.practicum.gateway.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.ItemRequestCreateDto;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestClient.createItemRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
    return itemRequestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable("requestId") Long requestId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getItemRequest(requestId, userId);
    }

}
