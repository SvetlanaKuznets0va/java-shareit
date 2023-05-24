package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private UserValidator userValidator;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserValidator userValidator) {
        this.userDao = userDao;
        this.userValidator = userValidator;
    }

    @Override
    public User addUser(User user) {
        userValidator.validateUser(user);
        return userDao.addUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    @Override
    public User findUserById(int id) {
        return userDao.findUserById(id);
    }

    @Override
    public void deleteUserById(int id) {
        userDao.deleteUserById(id);
    }
}
