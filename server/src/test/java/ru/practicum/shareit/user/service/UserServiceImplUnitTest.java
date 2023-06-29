package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @Mock
    UserDao userDao;

    @Test
    void shouldAddUser() {
        UserService userService = new UserServiceImpl(userDao);
        User userBefore = new User("op@pa.ru", "user1");
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.save(userBefore)).thenReturn(userAfter);

        UserDto result = userService.addUser(new UserDto(0, "op@pa.ru", "user1"));

        Mockito.verify(userDao, Mockito.times(1)).save(userBefore);
        assertEquals(userAfter.getId(), result.getId());
    }

    @Test
    void shouldGetAllUsers() {
        UserService userService = new UserServiceImpl(userDao);
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.findAll()).thenReturn(Collections.singletonList(userAfter));

        List<UserDto> result = userService.getAllUsers();

        Mockito.verify(userDao, Mockito.times(1)).findAll();
        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateUser() {
        UserService userService = new UserServiceImpl(userDao);
        User userBefore = new User("op@pa.ru", "user1");
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.save(userBefore)).thenReturn(userAfter);
        UserDto result = userService.addUser(new UserDto(0, "op@pa.ru", "user1"));
        Mockito.verify(userDao, Mockito.times(1)).save(userBefore);
        assertEquals(userAfter.getId(), result.getId());
        User userUp = new User(1, "po@ap.ru", "UpUser1");
        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(userAfter));
        Mockito.when(userDao.save(userUp)).thenReturn(userUp);

        result = userService.updateUser(new UserDto(0, "po@ap.ru", "UpUser1"), 1);

        assertEquals(1, result.getId());
        assertEquals("po@ap.ru", result.getEmail());
        assertEquals("UpUser1", result.getName());
    }

    @Test
    void shouldReturnNotFoundExceptionIfUserIdNotFoundInUpdateUser() {
        UserService userService = new UserServiceImpl(userDao);
        User userBefore = new User("op@pa.ru", "user1");
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.save(userBefore)).thenReturn(userAfter);
        UserDto result = userService.addUser(new UserDto(0, "op@pa.ru", "user1"));
        Mockito.verify(userDao, Mockito.times(1)).save(userBefore);
        assertEquals(userAfter.getId(), result.getId());

        assertThrows(NotFoundException.class, () -> userService.updateUser(new UserDto(0, "po@ap.ru", "UpUser1"), 2));
    }


    @Test
    void shouldFindUserById() {
        UserService userService = new UserServiceImpl(userDao);
        User userBefore = new User("op@pa.ru", "user1");
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.save(userBefore)).thenReturn(userAfter);
        UserDto result = userService.addUser(new UserDto(0, "op@pa.ru", "user1"));
        Mockito.verify(userDao, Mockito.times(1)).save(userBefore);
        assertEquals(userAfter.getId(), result.getId());

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(userAfter));

        result = userService.findUserById(1);

        assertEquals(1, result.getId());
        assertEquals("op@pa.ru", result.getEmail());
        assertEquals("user1", result.getName());
    }

    @Test
    void shouldReturnNotFoundExceptionIfUserIdNotFoundInFindUserById() {
        UserService userService = new UserServiceImpl(userDao);
        User userBefore = new User("op@pa.ru", "user1");
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.save(userBefore)).thenReturn(userAfter);
        UserDto result = userService.addUser(new UserDto(0, "op@pa.ru", "user1"));
        Mockito.verify(userDao, Mockito.times(1)).save(userBefore);
        assertEquals(userAfter.getId(), result.getId());

        assertThrows(NotFoundException.class, () -> userService.updateUser(new UserDto(0, "po@ap.ru", "UpUser1"), 2));
    }


    @Test
    void shouldDeleteUser() {
        UserService userService = new UserServiceImpl(userDao);
        User userBefore = new User("op@pa.ru", "user1");
        User userAfter = new User(1,"op@pa.ru", "user1");
        Mockito.when(userDao.save(userBefore)).thenReturn(userAfter);
        UserDto result = userService.addUser(new UserDto(0, "op@pa.ru", "user1"));
        Mockito.verify(userDao, Mockito.times(1)).save(userBefore);
        assertEquals(userAfter.getId(), result.getId());

        userService.deleteUserById(1);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(1));
    }
}