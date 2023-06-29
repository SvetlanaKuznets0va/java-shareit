package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ItemRequestDaoTest {
    @Autowired
    ItemRequestDao itemRequestDao;

    @Autowired
    UserDao userDao;

    @Test
    void shouldSaveRequest() {
        User user = prepareUser("op@pa.ru", "user");
        ItemRequest ir = itemRequestDao.save(new ItemRequest(0, "description", user.getId(), LocalDateTime.now()));
        assertTrue(ir.getId() != 0);
    }

    @Test
    void shouldFindItemRequestsByRequestorIdOrderByCreatedDesc() {
        User user = prepareUser("op@pa.ru", "user");
        List<ItemRequest> requests = prepareItemRequests(user, null);
        List<ItemRequest> response = itemRequestDao.findItemRequestsByRequestorIdOrderByCreatedDesc(user.getId());
        assertEquals(5, response.size());
        assertEquals(requests.get(0).getCreated(), response.get(4).getCreated());
        assertEquals(requests.get(4).getCreated(), response.get(0).getCreated());
    }

    @Test
    void shouldFindAllByOrderByCreatedDesc() {
        User user = prepareUser("op@pa.ru", "user");
        List<ItemRequest> requests = prepareItemRequests(user, null);
        List<ItemRequest> response = itemRequestDao.findAllByOrderByCreatedDesc();
        assertEquals(5, response.size());
        assertEquals(requests.get(0).getCreated(), response.get(4).getCreated());
        assertEquals(requests.get(4).getCreated(), response.get(0).getCreated());
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
        requests.add(new ItemRequest(0, "description2", user1.getId(), LocalDateTime.now().plusMinutes(10)));
        requests.add(new ItemRequest(0, "description3", user1.getId(), LocalDateTime.now().plusMinutes(20)));
        requests.add(new ItemRequest(0, "description4", user1.getId(), LocalDateTime.now().plusMinutes(30)));
        requests.add(new ItemRequest(0, "description5", user1.getId(), LocalDateTime.now().plusMinutes(40)));
        itemRequestDao.saveAll(requests);
        return requests;
    }
}