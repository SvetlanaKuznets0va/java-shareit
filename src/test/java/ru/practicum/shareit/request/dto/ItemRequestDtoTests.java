package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
class ItemRequestDtoTests {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
    LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(dtf));

    @Test
    void testItemRequestDto() throws IOException {
        ItemRequest ir = new ItemRequest(1, "description", 1, created);

        JsonContent<ItemRequestDto> result = json.write(ItemRequestMapper.toItemRequestDto(ir));

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }
}