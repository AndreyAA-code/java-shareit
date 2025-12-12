package ru.practicum.server.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.ItemRequestCreateDto;
import ru.practicum.server.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
   public final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.create(itemRequestCreateDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
    return itemRequestService.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable("requestId") Long requestId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }

}
