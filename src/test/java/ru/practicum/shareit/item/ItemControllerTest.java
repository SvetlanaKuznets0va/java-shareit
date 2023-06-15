package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void shouldAddItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", null, null);
        ItemDto itemResp = new ItemDto(1, 1, "item1", "item1", true, null);
        when(service.addItem(itemReq, 1)).thenReturn(itemResp);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResp.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResp.getName())))
                .andExpect(jsonPath("$.description", is(itemResp.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResp.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemResp.getRequestId())));
    }

    @Test
    void shouldThrowNotFoundExceptionInAddItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", true, null);
        when(service.addItem(any(), anyInt())).thenThrow(new NotFoundException("Такого владельца нет"));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowValidationExceptionIfAvailableIsNullInAddItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", null, null);
        when(service.addItem(any(), anyInt())).thenThrow(new ValidationException("Не полная информация"));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void shouldThrowValidationExceptionInAddItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", null, null);
        when(service.addItem(any(), anyInt())).thenThrow(new ValidationException("Не полная информация"));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", null, null);
        ItemDto itemResp = new ItemDto(1, 1, "item1", "item1", true, null);
        when(service.updateItem(itemReq, 1, 1)).thenReturn(itemResp);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResp.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResp.getName())))
                .andExpect(jsonPath("$.description", is(itemResp.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResp.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemResp.getRequestId())));
    }

    @Test
    void shouldThrowNotFoundExceptionInUpdateItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", true, null);
        when(service.updateItem(itemReq, 2, 1)).thenThrow(new NotFoundException("Такого владельца нет"));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowNotFoundExceptionIfItemNotFoundInUpdateItem() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", true, null);
        when(service.updateItem(itemReq, 2, 1)).thenThrow(new NotFoundException("Такой вещи нет"));

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldGetItemById() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", null, null);

        ItemDtoPers itemResp = new ItemDtoPers(1, 1, "item1", "item1", true, null, null,
                Collections.singletonList(new CommentDto(1, "text", "user", LocalDateTime.now())), null);
        when(service.findItemByIdAndUserId(1, 1)).thenReturn(itemResp);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResp.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResp.getName())))
                .andExpect(jsonPath("$.description", is(itemResp.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResp.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemResp.getRequestId())))
                .andExpect(jsonPath("$.comments[0].id", is(itemResp.getComments().get(0).getId())));
    }

    @Test
    void shouldThrowNotFoundExceptionInGetItemById() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", true, null);
        when(service.findItemByIdAndUserId(2, 1)).thenThrow(new NotFoundException("Такой вещи нет"));

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldGetItemByUserIdWithPagebale() throws Exception {
        ItemDto itemReq = new ItemDto(null, null, "item1", "item1", null, null);

        ItemDtoPers itemResp = new ItemDtoPers(1, 1, "item1", "item1", true, null, null,
                Collections.singletonList(new CommentDto(1, "text", "user", LocalDateTime.now())), null);
        when(service.findItemsByUserId(0, 2, 1)).thenReturn(Collections.singletonList(itemResp));

        mvc.perform(get("/items", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "2")
                        .content(mapper.writeValueAsString(itemReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResp.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResp.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResp.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemResp.getRequestId())))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemResp.getComments().get(0).getId())));
    }

    @Test
    void searchItemsByText() throws Exception {
        ItemDto itemResp = new ItemDto(1, 1, "item1", "item1", true, null);
        when(service.searchItemsByText(0, 2, "Item1")).thenReturn(Collections.singletonList(itemResp));

        mvc.perform(get("/items/search")
                        .param("text", "Item1")
                        .param("from", "0")
                        .param("size", "2")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemResp.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResp.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResp.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemResp.getRequestId())));
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentDto comment = new CommentDto(0, "Item1", null, null);
        CommentDto commentResp = new CommentDto(1, "Item1", "user",
                LocalDateTime.of(2023, 3, 14, 13, 10, 0));

        when(service.addComment(1, 1, comment)).thenReturn(commentResp);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResp.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentResp.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResp.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentResp.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));
    }
}