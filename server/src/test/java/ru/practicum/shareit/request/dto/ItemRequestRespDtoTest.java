package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemRequestRespDtoTest {
    @Autowired
    private JacksonTester<ItemRequestRespDto> json;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");
    LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(dtf));

    @Test
    void testItemRequestDtoResp() throws IOException {
        ItemRequest ir = new ItemRequest(1, "description", 1, created);
        List<Item> items = new ArrayList<>();
        items.add(new Item(1, 1, "item1", "item1", true, 1));
        items.add(new Item(2, 2, "item2", "item2", true, null));
        items.add(new Item(3, 3, "item3", "item3", true, 3));
        items.add(new Item(4, 4, "item4", "item4", true, 1));

        JsonContent<ItemRequestRespDto> result = json.write(ItemRequestMapper.toItemRequestRespDto(ir, items));

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(items.get(0).getId());
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo(items.get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items.[0].description").isEqualTo(items.get(0).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items.[0].available").isEqualTo(items.get(0).isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].requestId").isEqualTo(items.get(0).getRequestId());
        assertThat(result).extractingJsonPathNumberValue("$.items.[1].id").isEqualTo(items.get(3).getId());
        assertThat(result).extractingJsonPathStringValue("$.items.[1].name").isEqualTo(items.get(3).getName());
        assertThat(result).extractingJsonPathStringValue("$.items.[1].description").isEqualTo(items.get(3).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items.[1].available").isEqualTo(items.get(3).isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items.[1].requestId").isEqualTo(items.get(3).getRequestId());
    }
}