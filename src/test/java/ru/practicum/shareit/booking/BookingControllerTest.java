package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService service;

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

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dtf));
    LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(50).format(dtf));

    @Test
    void shouldAddBooking() throws Exception {
        UserDto booker = new UserDto(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        BookingDto bookingDtoReq = new BookingDto(0, start, end, 1, null, null);
        BookingDtoResp bookingDtoResp = new BookingDtoResp(1, start, end, item, booker, Status.WAITING);
        when(service.add(bookingDtoReq, 1)).thenReturn(bookingDtoResp);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
    }

    @Test
    void shouldApprove() throws Exception {
        UserDto booker = new UserDto(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        BookingDtoResp bookingDtoResp = new BookingDtoResp(1, start, end, item, booker, Status.APPROVED);
        when(service.approve(2, 1, true)).thenReturn(bookingDtoResp);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void shouldGetByBookingId() throws Exception {
        UserDto booker = new UserDto(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        BookingDtoResp bookingDtoResp = new BookingDtoResp(1, start, end, item, booker, Status.APPROVED);
        when(service.getByBookingId(1, 1)).thenReturn(bookingDtoResp);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(start.toString())))
                .andExpect(jsonPath("$.end", is(end.toString())))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(1)))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void shouldGetAllForUser() throws Exception {
        UserDto booker = new UserDto(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        BookingDtoResp bookingDtoResp = new BookingDtoResp(1, start, end, item, booker, Status.APPROVED);
        when(service.getAllForUser(0, 2, 1, "ALL")).thenReturn(Collections.singletonList(bookingDtoResp));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "2")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(start.toString())))
                .andExpect(jsonPath("$[0].end", is(end.toString())))
                .andExpect(jsonPath("$[0].item.id", is(1)))
                .andExpect(jsonPath("$[0].booker.id", is(1)))
                .andExpect(jsonPath("$[0].status", is(Status.APPROVED.toString())));
    }

    @Test
    void shouldGetAllForOwner() throws Exception {
        UserDto booker = new UserDto(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        BookingDtoResp bookingDtoResp = new BookingDtoResp(1, start, end, item, booker, Status.APPROVED);
        when(service.getAllForOwner(0, 2, 2, "ALL")).thenReturn(Collections.singletonList(bookingDtoResp));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", "0")
                        .param("size", "2")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].start", is(start.toString())))
                .andExpect(jsonPath("$[0].end", is(end.toString())))
                .andExpect(jsonPath("$[0].item.id", is(1)))
                .andExpect(jsonPath("$[0].booker.id", is(1)))
                .andExpect(jsonPath("$[0].status", is(Status.APPROVED.toString())));
    }
}