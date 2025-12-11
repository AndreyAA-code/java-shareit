package ru.practicum.gateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.client.RestTemplateConfig;
import ru.practicum.gateway.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private static final String URL = "/requests";

    @Autowired
    public ItemRequestClient(RestTemplate restTemplate, RestTemplateConfig config) {
        super(restTemplate, config);
    }

    public ResponseEntity<Object> createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        return post(URL, userId, itemRequestCreateDto);
    }

    public ResponseEntity<Object> getOwnItemRequests(Long userId) {
        return get(URL, userId);
    }

    public ResponseEntity<Object> getAllItemRequests(Long userId) {
        return get(URL + "/all", userId);
    }

    public ResponseEntity<Object> getItemRequest(Long requestId, Long userId) {
        String path = URL + "/" + requestId;
        return get(path, userId);
    }

}
