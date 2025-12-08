package ru.practicum.server.controler_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.item.ItemController;
import ru.practicum.server.item.dto.comment.CommentDto;
import ru.practicum.server.item.dto.item.ItemCommentsLastNextBookingDto;
import ru.practicum.server.item.dto.item.ItemCreateDto;
import ru.practicum.server.item.dto.item.ItemDto;
import ru.practicum.server.item.dto.item.ItemUpdateDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.exceptions.NotFoundException;

import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long userId = 1L;
    private final Long itemId = 100L;

    @Test
    void shouldGetAllItems() throws Exception {
        ItemCommentsLastNextBookingDto itemDto = new ItemCommentsLastNextBookingDto();
        itemDto.setId(itemId);
        itemDto.setName("Test Item");

        when(itemService.getItems(userId)).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Test Item"));

        verify(itemService, times(1)).getItems(userId);
    }

    @Test
    void shouldGetItemById() throws Exception {
        ItemCommentsLastNextBookingDto itemDto = new ItemCommentsLastNextBookingDto();
        itemDto.setId(itemId);
        itemDto.setName("Specific Item");

        when(itemService.getItemById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Specific Item"));

        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @Test
    void shouldCreateItem() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("New Item");
        createDto.setDescription("Description");
        createDto.setAvailable(true);

        ItemDto savedDto = new ItemDto();
        savedDto.setId(itemId);
        savedDto.setName("New Item");

        when(itemService.createItem(any(ItemCreateDto.class), eq(userId))).thenReturn(savedDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("New Item"));

        verify(itemService, times(1)).createItem(any(ItemCreateDto.class), eq(userId));
    }

    @Test
    void shouldUpdateItemById() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto();
        updateDto.setName("Updated Item");
        updateDto.setDescription("Updated description");
        updateDto.setAvailable(false);

        ItemDto updatedDto = new ItemDto();
        updatedDto.setId(itemId);
        updatedDto.setName("Updated Item");

        when(itemService.updateItemById(eq(itemId), any(ItemUpdateDto.class), eq(userId)))
                .thenReturn(updatedDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Item"));

        verify(itemService, times(1)).updateItemById(eq(itemId), any(ItemUpdateDto.class), eq(userId));
    }

    @Test
    void shouldSearchAvailableItems() throws Exception {
        ItemDto searchResult = new ItemDto();
        searchResult.setId(itemId);
        searchResult.setName("Search Result");

        when(itemService.searchItemsByNameAndDescription("test", userId))
                .thenReturn(Collections.singletonList(searchResult));

        mockMvc.perform(get("/items/search")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemId))
                .andExpect(jsonPath("$[0].name").value("Search Result"));

        verify(itemService, times(1)).searchItemsByNameAndDescription("test", userId);
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        CommentDto savedComment = new CommentDto();
        savedComment.setId(1L);
        savedComment.setText("Great item!");
        savedComment.setAuthorName("John");

        when(itemService.addComment(eq(itemId), eq(userId), any(CommentDto.class)))
                .thenReturn(savedComment);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"))
                .andExpect(jsonPath("$.authorName").value("John"));

        verify(itemService, times(1)).addComment(eq(itemId), eq(userId), any(CommentDto.class));
    }

    @Test
    void shouldReturnNotFoundWhenItemDoesNotExist() throws Exception {
        when(itemService.getItemById(999L, userId)).thenThrow(new NotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItemById(999L, userId);
    }

    @Test
    void shouldValidateOnCreateItemWithNullName() throws Exception {
        ItemCreateDto invalidDto = new ItemCreateDto();
        invalidDto.setName(null);
        invalidDto.setDescription("Description");
        invalidDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(any(), eq(userId));
    }

    @Test
    void shouldValidateOnCreateItemWithEmptyName() throws Exception {
        ItemCreateDto invalidDto = new ItemCreateDto();
        invalidDto.setName("");
        invalidDto.setDescription("Description");
        invalidDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(any(), eq(userId));
    }

    @Test
    void shouldReturnBadRequestWhenAddingCommentWithEmptyText() throws Exception {
        CommentDto invalidComment = new CommentDto();
        invalidComment.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(eq(itemId), eq(userId), any());
    }

    @Test
    void shouldReturnBadRequestWhenAddingCommentWithNullText() throws Exception {
        CommentDto invalidComment = new CommentDto();
        invalidComment.setText(null);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(eq(itemId), eq(userId), any());
    }

    @Test
    void shouldHandleNullUserIdHeader() throws Exception {
        mockMvc.perform(get("/items")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItems(anyLong());
    }

    @Test
    void shouldHandleMissingUserIdHeaderOnCreate() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto();
        createDto.setName("New Item");
        createDto.setDescription("Description");
        createDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(any(), anyLong());
    }
}