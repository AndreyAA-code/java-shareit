package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.client.RestTemplateConfig;
import ru.practicum.gateway.item.dto.comment.CommentDto;
import ru.practicum.gateway.item.dto.item.ItemCreateDto;
import ru.practicum.gateway.item.dto.item.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {

    private static final String URL = "/items";

    @Autowired
    public ItemClient(RestTemplate restTemplate, RestTemplateConfig config) {
        super(restTemplate, config);
    }

    public ResponseEntity<Object> getItems(Long userId) {
        return get(URL, userId);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get(URL + "/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(ItemCreateDto itemCreateDto, Long userId) {
        return post(URL, userId, itemCreateDto);
    }

    public ResponseEntity<Object> updateItemById(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        return patch(URL + "/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> searchAvailableItems(String text, Long userId) {
        return get(URL + "?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(Long itemId, CommentDto commentDto, Long userId) {
        return post(URL + "/" + itemId + "/comment", userId, commentDto);
    }

}
