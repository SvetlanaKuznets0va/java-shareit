package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
    LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(dtf));

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void shouldAddItemRequest() throws Exception {
        ItemRequestDto irReq = new ItemRequestDto(0, "description", null);
        ItemRequestDto irResp = new ItemRequestDto(1, "description", created);
        when(service.addItemRequest(any(), anyInt())).thenReturn(irResp);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(irReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(irResp.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(irResp.getDescription())))
                .andExpect(jsonPath("$.created", is(irResp.getCreated().toString())));
    }

    @Test
    void shouldThrowNotFoundExceptionInAddItemRequest() throws Exception {
        ItemRequestDto irReq = new ItemRequestDto(0, "description", null);
        when(service.addItemRequest(any(), anyInt())).thenThrow(new NotFoundException("Такого владельца нет"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(irReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowValidationExceptionInAddItemRequest() throws Exception {
        ItemRequestDto irReq = new ItemRequestDto(0, "description", null);
        when(service.addItemRequest(any(), anyInt())).thenThrow(new ValidationException("Валидация не пройдена"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(irReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void shouldThrowExceptionInAddItemRequest() throws Exception {
        ItemRequestDto irReq = new ItemRequestDto(0, "description", null);
        when(service.addItemRequest(any(), anyInt())).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(irReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    void shouldGetOwnItemRequests() throws Exception {
        ItemRequestRespDto irResp = new ItemRequestRespDto(1, "description", created, null);
        when(service.getOwnItemRequests(anyInt())).thenReturn(Collections.singletonList(irResp));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(irResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(irResp.getDescription())))
                .andExpect(jsonPath("$[0].created", is(irResp.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(irResp.getItems())));
    }

    @Test
    void shouldThrowNotFoundExceptionInGetOwnItemRequests() throws Exception {
        when(service.getOwnItemRequests(anyInt())).thenThrow(new NotFoundException("Такого владельца нет"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowExceptionInGetOwnItemRequests() throws Exception {
        when(service.getOwnItemRequests(anyInt())).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    void shouldGetAllItemRequests() throws Exception {
        ItemRequestRespDto irResp = new ItemRequestRespDto(1, "description", created, null);
        when(service.getAllItemRequests(anyInt(), anyInt(), anyInt())).thenReturn(Collections.singletonList(irResp));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "5")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(irResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(irResp.getDescription())))
                .andExpect(jsonPath("$[0].created", is(irResp.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(irResp.getItems())));
    }

    @Test
    void shouldThrowExceptionInGetAllItemRequests() throws Exception {
        when(service.getAllItemRequests(anyInt(), anyInt(), anyInt())).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "5")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    @Test
    void shouldGetItemRequest() throws Exception {
        ItemRequestRespDto irResp = new ItemRequestRespDto(1, "description", created, null);
        when(service.getItemRequestById(anyInt(), anyInt())).thenReturn(irResp);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(irResp.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(irResp.getDescription())))
                .andExpect(jsonPath("$.created", is(irResp.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(irResp.getItems())));
    }

    @Test
    void shouldThrowNotFoundExceptionInGetItemRequest() throws Exception {
        when(service.getItemRequestById(anyInt(), anyInt())).thenThrow(new NotFoundException("Запрос или юзер не найден"));

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowExceptionInGetItemRequest() throws Exception {
        when(service.getItemRequestById(anyInt(), anyInt())).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }

    @Test
    void shouldGetItemRequests() throws Exception {
        ItemRequestRespDto irResp = new ItemRequestRespDto(1, "description", created, null);
        when(service.getItemRequests()).thenReturn(Collections.singletonList(irResp));

        mvc.perform(get("/requests")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(irResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(irResp.getDescription())))
                .andExpect(jsonPath("$[0].created", is(irResp.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(irResp.getItems())));

    }
}