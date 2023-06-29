package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrTest {
    private final EntityManager em;
    @Autowired
    private ItemService service;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookingDao bookingDao;

    @Test
    void addItem() {
        User user = prepareUser("op@pa.ru", "user");
        ItemDto itemDto = new ItemDto(0, user.getId(), "item", "item description", true, null);

        ItemDto result = service.addItem(itemDto, user.getId());

        assertThat(result.getOwnerId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo("item"));
        assertThat(result.getDescription(), equalTo("item description"));
    }

    @Test
    void updateItem() {
        User user = prepareUser("op@pa.ru", "user");
        ItemDto itemDto = new ItemDto(0, user.getId(), "item", "item description", true, null);
        ItemDto itemDtoUpd = new ItemDto(0, user.getId(), "item upd", "item description upd", true, null);

        ItemDto resultSave = service.addItem(itemDto, user.getId());
        ItemDto resultUpd = service.updateItem(itemDtoUpd, user.getId(), resultSave.getId());

        assertThat(resultUpd.getOwnerId(), equalTo(user.getId()));
        assertThat(resultUpd.getName(), equalTo("item upd"));
        assertThat(resultUpd.getDescription(), equalTo("item description upd"));
    }

    @Test
    void findItemById() {
        User user = prepareUser("op@pa.ru", "user");
        ItemDto itemDto = new ItemDto(0, user.getId(), "item", "item description", true, null);

        ItemDto resultSave = service.addItem(itemDto, user.getId());
        ItemDto result = service.findItemById(resultSave.getId());

        assertThat(result.getId(), equalTo(resultSave.getId()));
        assertThat(result.getOwnerId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo("item"));
        assertThat(result.getDescription(), equalTo("item description"));
    }

    @Test
    void findItemByIdAndUserId() {
        User user = prepareUser("op@pa.ru", "user");
        ItemDto itemDto = new ItemDto(0, user.getId(), "item", "item description", true, null);

        ItemDto resultSave = service.addItem(itemDto, user.getId());
        ItemDtoPers result = service.findItemByIdAndUserId(user.getId(), resultSave.getId());

        assertThat(result.getId(), equalTo(resultSave.getId()));
        assertThat(result.getOwnerId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo("item"));
        assertThat(result.getDescription(), equalTo("item description"));
        assertThat(result.getLastBooking(), equalTo(null));
        assertThat(result.getNextBooking(), equalTo(null));
        assertThat(result.getComments(), equalTo(Collections.emptyList()));
        assertThat(result.getRequestId(), equalTo(null));
    }

    @Test
    void findItemsByUserId() {
        User user = prepareUser("op@pa.ru", "user");
        ItemDto itemDto1 = new ItemDto(0, user.getId(), "item1", "item description1", true, null);
        ItemDto itemDto2 = new ItemDto(0, user.getId(), "item2", "item description2", true, null);
        service.addItem(itemDto1, user.getId());
        service.addItem(itemDto2, user.getId());

        List<ItemDtoPers> result = service.findItemsByUserId(0, 2, user.getId());

        assertThat(result.size(), equalTo(2));
    }

    @Test
    void searchItemsByText() {
        User user = prepareUser("op@pa.ru", "user");
        ItemDto itemDto1 = new ItemDto(0, user.getId(), "item1", "item description1", true, null);
        ItemDto itemDto2 = new ItemDto(0, user.getId(), "item2", "item description2", true, null);
        service.addItem(itemDto1, user.getId());
        service.addItem(itemDto2, user.getId());

        List<ItemDto> result = service.searchItemsByText(0, 2, "ITem1");
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getName(), equalTo("item1"));
    }

    @Test
    void addComment() {
        User user = prepareUser("op@pa.ru", "user");
        User booker = prepareUser("po@ap.ru", "user booker");
        ItemDto itemDto = new ItemDto(0, user.getId(), "item1", "item description1", true, null);
        Item item = ItemMapper.toItem(service.addItem(itemDto, user.getId()));

        Booking booking = new Booking(0, LocalDateTime.of(2023, 2, 3, 3, 3),
                LocalDateTime.of(2023, 2, 4, 3, 3), item, booker, Status.APPROVED);
        bookingDao.save(booking);
        CommentDto comment = new CommentDto(0, "Comment", null, null);
        CommentDto result = service.addComment(booker.getId(), item.getId(), comment);

        assertThat(result.getText(), equalTo("Comment"));
    }

    private User prepareUser(String email, String name) {
        List<User> users = userDao.findAll();

        boolean checkFall = users.stream().anyMatch(u -> u.getEmail().equals(email));
        if (checkFall) {
            throw new RuntimeException("Дублирование email ползьователя");
        }
        userDao.save(new User(0, email, name));
        users = userDao.findAll();
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst().orElseThrow(() -> new RuntimeException("Юзер не найден"));
    }
}