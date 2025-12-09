package ru.practicum.gateway.request;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String URL = "/requests";

    @Value("${server.port}")
    private String host;

    @Autowired
    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createItemRequest(@Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
                                                    @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return post(host + URL, userId, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return get(host + URL, userId);
    }

    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return get(host + URL + "/all", userId);
    }

    public ResponseEntity<Object> getItemRequest(@PathVariable("requestId") Long requestId,
                                                 @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        String path = host + URL + "/" + requestId;
        return get(path, userId);
    }


}
