package ru.practicum.server.controler_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.request.ItemRequestController;
import ru.practicum.server.request.dto.ItemRequestCreateDto;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.ItemRequestService;
import static org.hamcrest.Matchers.hasSize;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long requestId = 100L;

    @Test
    void shouldCreateItemRequest_Success() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need a tool for repair");

        ItemRequestDto savedDto = ItemRequestDto.builder()
                .id(requestId)
                .description(createDto.getDescription())
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.create(any(ItemRequestCreateDto.class), eq(userId)))
                .thenReturn(savedDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a tool for repair"))
                .andExpect(jsonPath("$.created").isNotEmpty());

        verify(itemRequestService, times(1)).create(any(ItemRequestCreateDto.class), eq(userId));
    }

    @Test
    void shouldReturnBadRequest_WhenDescriptionEmpty() throws Exception {
        ItemRequestCreateDto invalidDto = new ItemRequestCreateDto();
        invalidDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(any(), eq(userId));
    }

    @Test
    void shouldReturnBadRequest_WhenDescriptionNull() throws Exception {
        ItemRequestCreateDto invalidDto = new ItemRequestCreateDto();
        invalidDto.setDescription(null);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(any(), eq(userId));
    }

    @Test
    void shouldGetOwnItemRequests_Success() throws Exception {
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder().id(101L).description("Tool").build(),
                ItemRequestDto.builder().id(102L).description("Ladder").build()
        );

        when(itemRequestService.getOwnItemRequests(eq(userId))).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value("Tool"))
                .andExpect(jsonPath("$[1].description").value("Ladder"));


        verify(itemRequestService, times(1)).getOwnItemRequests(eq(userId));
    }

    @Test
    void shouldGetAllItemRequests_Success() throws Exception {
        List<ItemRequestDto> requests = List.of(
                ItemRequestDto.builder().id(201L).description("Hammer").build(),
                ItemRequestDto.builder().id(202L).description("Screwdriver").build()
        );

        when(itemRequestService.getAllItemRequests(eq(userId))).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value("Hammer"))
                .andExpect(jsonPath("$[1].description").value("Screwdriver"));


        verify(itemRequestService, times(1)).getAllItemRequests(eq(userId));
    }

    @Test
    void shouldGetItemRequest_Success() throws Exception {
        ItemRequestDto expectedDto = ItemRequestDto.builder()
                .id(requestId)
                .description("Drill")
                .created(LocalDateTime.now())
                .build();

        when(itemRequestService.getItemRequest(eq(requestId), eq(userId))).thenReturn(expectedDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Drill"))
                .andExpect(jsonPath("$.created").isNotEmpty());

        verify(itemRequestService, times(1)).getItemRequest(eq(requestId), eq(userId));
    }

    @Test
    void shouldReturnNotFound_WhenRequestDoesNotExist() throws Exception {
        when(itemRequestService.getItemRequest(eq(999L), eq(userId)))
                .thenThrow(new RuntimeException("Request not found"));

        mockMvc.perform(get("/requests/999", 999L)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        verify(itemRequestService, times(1)).getItemRequest(eq(999L), eq(userId));
    }
}
