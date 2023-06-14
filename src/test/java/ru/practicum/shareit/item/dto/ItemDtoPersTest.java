package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemDtoPersTest {
    @Autowired
    private JacksonTester<ItemDtoPers> json;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime lastStart = LocalDateTime.parse(LocalDateTime.now().minusMinutes(10).format(dtf));
    LocalDateTime lastEnd = LocalDateTime.parse(LocalDateTime.now().format(dtf));
    LocalDateTime nextStart = LocalDateTime.parse(LocalDateTime.now().format(dtf));
    LocalDateTime nextEnd = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dtf));
    LocalDateTime createdComment = LocalDateTime.parse(LocalDateTime.now().format(dtf));
    @Test
    void testItemDtoPers() throws IOException {
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 1, "item1", "item1", true, 2);
        Booking last = new Booking(1, lastStart, lastEnd, item, booker, Status.APPROVED);
        Booking next = new Booking(2, nextStart, nextEnd, item, booker, Status.WAITING);
        List<CommentDto> comments = Collections.singletonList(
                new CommentDto(1, "comment", "author", createdComment));

        JsonContent<ItemDtoPers> result = json.write(ItemMapper.toItemDtoPers(item, last, next, comments));

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item1");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo(lastStart.toString());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo(lastEnd.toString());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo(Status.APPROVED.toString());

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo(nextStart.toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo(nextEnd.toString());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo(Status.WAITING.toString());

        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo(createdComment.toString());

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(2);
    }
}