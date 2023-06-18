package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserDaoTest {
    @Autowired
    UserDao userDao;

    @Test
    void shouldSaveUser() {
        User user = userDao.save(new User(1, "op@pa.ru", "user"));
        assertTrue(user.getId() != 0);
    }

    @Test
    void shouldFindUserById() {
        User user = userDao.save(new User(1, "op@pa.ru", "user"));
        User fUser = userDao.findById(user.getId()).get();
        assertTrue(fUser.getId() == user.getId());
    }

    @Test
    void shouldFindAll() {
        List<User> users = new ArrayList<>();
        users.add(new User("dd@dd", "dd"));
        users.add(new User("dada@dada", "dada"));
        users.add(new User("duda@duda", "duda"));
        userDao.saveAll(users);
        List<User> fUsers = userDao.findAll();
        assertTrue(fUsers.size() == 3);
    }
}