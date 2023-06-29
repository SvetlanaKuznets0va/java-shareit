package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService service;

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
    void addUser() throws Exception {
        UserDto userDtoReq = new UserDto(0, "op@pa.ru", "user");
        UserDto userDtoResp = new UserDto(1, "op@pa.ru", "user");

        when(service.addUser(userDtoReq)).thenReturn(userDtoResp);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDtoResp.getEmail())))
                .andExpect(jsonPath("$.name", is(userDtoResp.getName())));
    }

    @Test
    void shouldThrowValidationExceptionInAddUser() throws Exception {
        UserDto userDtoReq = new UserDto(0, "op@pa.ru", "user");

        when(service.addUser(userDtoReq)).thenThrow(new ValidationException("Валидация не пройдена"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void shouldThrowExceptionInAddUser() throws Exception {
        UserDto userDtoReq = new UserDto(0, "op@pa.ru", "user");

        when(service.addUser(userDtoReq)).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    void getAllUsers() throws Exception {
        UserDto userDtoResp = new UserDto(1, "op@pa.ru", "user");

        when(service.getAllUsers()).thenReturn(Collections.singletonList(userDtoResp));

        mvc.perform(get("/users")
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$[0].email", is(userDtoResp.getEmail())))
                .andExpect(jsonPath("$[0].name", is(userDtoResp.getName())));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDtoReq = new UserDto(0, "op@pa.ru", "user");
        UserDto userDtoResp = new UserDto(1, "op@pa.ru", "user");

        when(service.updateUser(userDtoReq, 1)).thenReturn(userDtoResp);

        mvc.perform(patch("/users/{id}", 1)
                        .content(mapper.writeValueAsString(userDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDtoResp.getEmail())))
                .andExpect(jsonPath("$.name", is(userDtoResp.getName())));
    }

    @Test
    void shouldThrowValidationExceptionInUpdateUser() throws Exception {
        UserDto userDtoReq = new UserDto(0, "op@pa.ru", "user");
        UserDto userDtoResp = new UserDto(1, "op@pa.ru", "user");

        when(service.updateUser(userDtoReq, 9)).thenThrow(new NotFoundException("Такого владельца нет"));

        mvc.perform(patch("/users/{id}", 9)
                        .content(mapper.writeValueAsString(userDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowExceptionInUpdateUser() throws Exception {
        UserDto userDtoReq = new UserDto(0, "op@pa.ru", "user");
        UserDto userDtoResp = new UserDto(1, "op@pa.ru", "user");

        when(service.updateUser(userDtoReq, 9)).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(patch("/users/{id}", 9)
                        .content(mapper.writeValueAsString(userDtoReq))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }


    @Test
    void deleteUserById() throws Exception {
        mvc.perform(delete("/users/{id}", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findUserById() throws Exception {
        UserDto userDtoResp = new UserDto(1, "op@pa.ru", "user");

        when(service.findUserById(1)).thenReturn(userDtoResp);

        mvc.perform(get("/users/{id}", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResp.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDtoResp.getEmail())))
                .andExpect(jsonPath("$.name", is(userDtoResp.getName())));
    }

    @Test
    void shouldThrowNotFoundExceptionInFindUserById() throws Exception {
        when(service.findUserById(1)).thenThrow(new NotFoundException("Такого владельца нет"));

        mvc.perform(get("/users/{id}", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void shouldThrowExceptionInFindUserById() throws Exception {
        when(service.findUserById(1)).thenThrow(new RuntimeException("Что то пошло не так"));

        mvc.perform(get("/users/{id}", 1)
                        .content("")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(500));
    }
}