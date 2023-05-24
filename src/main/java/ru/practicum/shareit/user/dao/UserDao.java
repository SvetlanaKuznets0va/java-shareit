package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    List<User> getAllUsers();

    User updateUser(User user);

    User findUserById(int id);

    void deleteUserById(int id);
}
