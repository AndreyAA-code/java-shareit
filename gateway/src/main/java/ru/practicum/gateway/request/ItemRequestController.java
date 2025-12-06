package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.request.dto.ItemRequestCreateDto;
import ru.practicum.gateway.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    @PostMapping
    public ItemRequestDto createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                            @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return null;
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
    return null;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return null;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable("requestId") Long requestId,
                                         @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return null;
    }

}
