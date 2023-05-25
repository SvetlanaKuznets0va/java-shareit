package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User addUser(UserDto userDto);

    List<User> getAllUsers();

    User updateUser(UserDto userDto, int id);

    User findUserById(int id);

    void deleteUserById(int id);
}
