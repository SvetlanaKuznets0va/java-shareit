package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        UserValidator.validateUser(userDto);
        User user = UserMapper.toUserWithoutId(userDto);
        return UserMapper.toUserDto(userDao.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User userBefore = UserMapper.toUser(findUserById(id));
        if (userBefore == null) {
            return null;
        }
        User userAfter = UserMapper.combineUserWithUserDto(userBefore, userDto);
        return UserMapper.toUserDto(userDao.save(userAfter));
    }

    @Override
    public UserDto findUserById(long id) {
        try {
            return UserMapper.toUserDto(userDao.findById(id).get());
        } catch (Exception e) {
            throw new NotFoundException("Такой пользователь не найден");
        }
    }

    @Override
    public void deleteUserById(long id) {
        userDao.deleteById(id);
    }

    @Override
    public boolean isExist(long id) {
        return findUserById(id) != null;
    }
}
