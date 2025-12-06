package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.exceptions.NotFoundException;
import ru.practicum.server.item.dto.item.ItemForItemRequestsDto;
import ru.practicum.server.item.dto.item.ItemMapper;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.request.dto.ItemRequestCreateDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestMapper;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
        private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(ItemRequestCreateDto itemRequestCreateDto, Long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + "not found"));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestCreateDto);
        itemRequest.setRequestor(requestor);
        return ItemRequestMapper.mapToItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Long userId) {   //список своих запросов
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + "not found"));

        List<ItemRequest> requests = itemRequestRepository
                .findByRequestorIdOrderByCreatedDesc(userId);

        return requests.stream()
                .map(request -> {
                    List<ItemForItemRequestsDto> items = itemRepository.findByRequestId(request.getId())
                            .stream()
                            .map(ItemMapper::mapItemToItemForItemRequestsDto)
                            .collect(Collectors.toList());

                    return ItemRequestDto.builder()
                            .id(request.getId())
                            .description(request.getDescription())
                            .created(request.getCreated())
                            .items(items)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) { //список всех запросов, кроме своих
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + "not found"));
        return itemRequestRepository.findAllByRequestorIdNot(userId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(Long requestId, Long userId) { //запрос запроса по ИД
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + "not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item with id " + requestId + "not found"));

        List<ItemForItemRequestsDto> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapItemToItemForItemRequestsDto)
                .collect(Collectors.toList());


        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }
}
