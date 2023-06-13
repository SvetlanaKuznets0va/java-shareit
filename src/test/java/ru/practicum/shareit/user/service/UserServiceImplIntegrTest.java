package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrTest {
    private final EntityManager em;
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Test
    void addUser() {
        UserDto user = new UserDto(0,"op@pa.ru", "user1");

        userService.addUser(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User result = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(result.getEmail(), equalTo("op@pa.ru"));
    }

    @Test
    void getAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(0,"op@pa.ru", "user1"));
        users.add(new User(0,"po@ap.ru", "user2"));

        userDao.saveAll(users);

        List<UserDto> ud = userService.getAllUsers();
        assertThat(ud.size(), equalTo(2));
        assertThat(ud.get(0).getEmail(), equalTo("op@pa.ru"));
        assertThat(ud.get(1).getEmail(), equalTo("po@ap.ru"));
    }

    @Test
    void updateUser() {
        UserDto user = new UserDto(0,"op@pa.ru", "user1");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User result = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(result.getEmail(), equalTo("op@pa.ru"));

        userService.updateUser(new UserDto(0, "opp@ppa.ru", "user1"), result.getId());
        query = em.createQuery("Select u from User u where u.id = :id", User.class);
        result = query.setParameter("id", result.getId()).getSingleResult();
        assertThat(result.getEmail(), equalTo("opp@ppa.ru"));
    }

    @Test
    void findUserById() {
        UserDto user = new UserDto(0,"op@pa.ru", "user1");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User result = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(result.getEmail(), equalTo("op@pa.ru"));

        UserDto resultDto = userService.findUserById(result.getId());
        assertThat(resultDto.getId(), equalTo(result.getId()));
        assertThat(resultDto.getEmail(), equalTo("op@pa.ru"));
    }

    @Test
    void deleteUserById() {
        UserDto user = new UserDto(0,"op@pa.ru", "user1");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User result = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(result.getEmail(), equalTo("op@pa.ru"));

        userService.deleteUserById(result.getId());

        assertThrows(NotFoundException.class, () -> userService.findUserById(result.getId()));
    }

    @Test
    void isExist(){
        UserDto user = new UserDto(0,"op@pa.ru", "user1");
        userService.addUser(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User result = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(result.getEmail(), equalTo("op@pa.ru"));

        boolean isExist = userService.isExist(result.getId());
        assertThat(isExist, equalTo(true));
    }
}
