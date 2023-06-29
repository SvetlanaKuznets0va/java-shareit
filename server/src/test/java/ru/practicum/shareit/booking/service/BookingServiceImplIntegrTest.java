package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrTest {
    @Autowired
    private BookingService service;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private ItemDao itemDao;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dtf));
    LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(50).format(dtf));

    @Test
    void addBooking() {
        User booker = prepareUser("op@pa.ru", "booker");
        User owner = prepareUser("po@ap.ru", "owner");
        Item item = new Item(0, owner.getId(), "item", "description", true, null);
        Item resultItem = itemDao.save(item);
        BookingDto bookingDto = new BookingDto(0, start, end, resultItem.getId(), null, null);

        BookingDtoResp result = service.add(bookingDto, booker.getId());

        assertThat(result.getItem().getId(), equalTo(resultItem.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void approveBooking() {
        User booker = prepareUser("op@pa.ru", "booker");
        User owner = prepareUser("po@ap.ru", "owner");
        Item item = new Item(0, owner.getId(), "item", "description", true, null);
        Item resultItem = itemDao.save(item);
        BookingDto bookingDto = new BookingDto(0, start, end, resultItem.getId(), null, null);

        BookingDtoResp before = service.add(bookingDto, booker.getId());
        BookingDtoResp result = service.approve(owner.getId(), before.getId(), true);

        assertThat(result.getItem().getId(), equalTo(resultItem.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void getByBookingId() {
        User booker = prepareUser("op@pa.ru", "booker");
        User owner = prepareUser("po@ap.ru", "owner");
        Item item = new Item(0, owner.getId(), "item", "description", true, null);
        Item resultItem = itemDao.save(item);
        BookingDto bookingDto = new BookingDto(0, start, end, resultItem.getId(), null, null);

        BookingDtoResp before = service.add(bookingDto, booker.getId());
        BookingDtoResp result = service.getByBookingId(booker.getId(), before.getId());

        assertThat(result.getItem().getId(), equalTo(resultItem.getId()));
        assertThat(result.getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getAllForUser() {
        User booker = prepareUser("op@pa.ru", "booker");
        User owner = prepareUser("po@ap.ru", "owner");
        Item item = new Item(0, owner.getId(), "item", "description", true, null);
        Item resultItem = itemDao.save(item);
        BookingDto bookingDto = new BookingDto(0, start, end, resultItem.getId(), null, null);

        service.add(bookingDto, booker.getId());

        List<BookingDtoResp> result = service.getAllForUser(0, 10, booker.getId(), "ALL");

        assertThat(result.get(0).getItem().getId(), equalTo(resultItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(0).getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getAllForOwner() {
        User booker = prepareUser("op@pa.ru", "booker");
        User owner = prepareUser("po@ap.ru", "owner");
        Item item = new Item(0, owner.getId(), "item", "description", true, null);
        Item resultItem = itemDao.save(item);
        BookingDto bookingDto = new BookingDto(0, start, end, resultItem.getId(), null, null);

        service.add(bookingDto, booker.getId());

        List<BookingDtoResp> result = service.getAllForOwner(0, 10, owner.getId(), "ALL");

        assertThat(result.get(0).getItem().getId(), equalTo(resultItem.getId()));
        assertThat(result.get(0).getBooker().getId(), equalTo(booker.getId()));
        assertThat(result.get(0).getStatus(), equalTo(Status.WAITING));
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

    private List<Item> prepareItems(User user1, User user2, List<ItemRequest> requests) {
        List<Item> items = new ArrayList<>();
        items.add(new Item(0, user2.getId(), "item1", "item1", true, null));
        items.add(new Item(0, user2.getId(), "item2", "item2", true, null));
        items.add(new Item(0, user2.getId(), "item3", "item3", true, null));
        items.add(new Item(0, user2.getId(), "item4", "item4", true, null));
        items.add(new Item(0, user2.getId(), "item5", "item5", true, null));
        itemDao.saveAll(items);
        return itemDao.findAll();
    }
}