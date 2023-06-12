package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrTest {
    private final EntityManager em;
    @Autowired
    private ItemRequestService service;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ItemRequestDao itemRequestDao;

    @Autowired
    private ItemDao itemDao;

    @Test
    void addItemRequest() {
        User user = prepareUser("op@pa.ru", "user1");
        ItemRequestDto itemRequestDto = new ItemRequestDto(0, "description", null);

        service.addItemRequest(itemRequestDto, user.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.requestorId = :id", ItemRequest.class);
        ItemRequest result = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(result.getDescription(), equalTo("description"));
    }

    @Test
    void getOwnItemRequests() {
        User user1 = prepareUser("op@pa.ru", "user1");
        User user2 = prepareUser("po@ap.ru", "user2");
        List<ItemRequest> requests = prepareItemRequests(user1, user2);
        prepareItems(user1, user2, requests);

        List<ItemRequestRespDto> result = service.getOwnItemRequests(user1.getId());

        assertThat(result.size(), equalTo(3));
        assertThat(result.get(0).getId(), equalTo(requests.get(0).getId()));
        assertThat(result.get(1).getId(), equalTo(requests.get(2).getId()));
        assertThat(result.get(2).getId(), equalTo(requests.get(4).getId()));
    }

    @Test
    void getItemRequests() {
        User user1 = prepareUser("op@pa.ru", "user1");
        User user2 = prepareUser("po@ap.ru", "user2");
        List<ItemRequest> requests = prepareItemRequests(user1, user2);
        prepareItems(user1, user2, requests);

        List<ItemRequestRespDto> result = service.getItemRequests();

        assertThat(result.size(), equalTo(5));
    }

    @Test
    void getAllItemRequests() {
        User user1 = prepareUser("op@pa.ru", "user1");
        User user2 = prepareUser("po@ap.ru", "user2");
        List<ItemRequest> requests = prepareItemRequests(user1, user2);
        prepareItems(user1, user2, requests);

        List<ItemRequestRespDto> result = service.getAllItemRequests(0, 5, user1.getId());

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getId(), equalTo(requests.get(1).getId()));
        assertThat(result.get(1).getId(), equalTo(requests.get(3).getId()));
    }

    @Test
    void getItemRequestById() {
        User user1 = prepareUser("op@pa.ru", "user1");
        User user2 = prepareUser("po@ap.ru", "user2");
        List<ItemRequest> requests = prepareItemRequests(user1, user2);
        prepareItems(user1, user2, requests);

        ItemRequestRespDto result = service.getItemRequestById(user1.getId(), requests.get(3).getId());
        assertThat(result.getId(), equalTo(requests.get(3).getId()));
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

    private List<ItemRequest> prepareItemRequests(User user1, User user2) {
        List<ItemRequest> requests = new ArrayList<>();
        requests.add(new ItemRequest(0, "description1", user1.getId(), LocalDateTime.now()));
        requests.add(new ItemRequest(0, "description2", user2.getId(), LocalDateTime.now()));
        requests.add(new ItemRequest(0, "description3", user1.getId(), LocalDateTime.now()));
        requests.add(new ItemRequest(0, "description4", user2.getId(), LocalDateTime.now()));
        requests.add(new ItemRequest(0, "description5", user1.getId(), LocalDateTime.now()));
        itemRequestDao.saveAll(requests);
        return itemRequestDao.findAll();
    }

    private List<Item> prepareItems(User user1, User user2, List<ItemRequest> requests) {
        List<Item> items = new ArrayList<>();
        items.add(new Item(0, user2.getId(), "item1", "item1", true, requests.get(0).getId()));
        items.add(new Item(0, user2.getId(), "item2", "item2", true, null));
        items.add(new Item(0, user2.getId(), "item3", "item3", true, requests.get(1).getId()));
        items.add(new Item(0, user2.getId(), "item4", "item4", true, null));
        items.add(new Item(0, user2.getId(), "item5", "item5", true, null));
        itemDao.saveAll(items);
        return itemDao.findAll();
    }
}