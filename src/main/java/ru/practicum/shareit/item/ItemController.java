package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {

    ItemService itemService;

    @GetMapping("")
    public Collection<ItemDto> getItems() {
        log.info("getItems()");
        return itemService.getItems();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@Valid @PathVariable Long itemId) {
        log.info("getItemById {}", itemId);
        return itemService.getItemById(itemId);
    }

    @PostMapping ("")
    public ItemDto createItem(@Valid @RequestBody Item item) {
        log.info("createItem()");
        return itemService.createItem(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@Valid @PathVariable Long itemId, @Valid @RequestBody Item item) {
        log.info("updateItemById()");
        return itemService.updateItemById(itemId, item);
    }

}
