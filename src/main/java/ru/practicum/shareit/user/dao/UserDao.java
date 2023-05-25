package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;

public interface UserDao {
    User addUser(UserDto userDto);

    List<User> getAllUsers();

    User updateUser(User user);

    User findUserById(int id);

    void deleteUserById(int id);

    Set<String> getEmails();
}
