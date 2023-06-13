package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        UserValidator.validateUser(userDto);
        User user = UserMapper.toUserWithoutId(userDto);
        UserDto result = UserMapper.toUserDto(userDao.save(user));
        log.info("Добавлен пользователь id={}", result.getId());
        return result;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User userBefore = UserMapper.toUser(findUserById(id));
        User userAfter = UserMapper.combineUserWithUserDto(userBefore, userDto);
        UserDto result = UserMapper.toUserDto(userDao.save(userAfter));
        log.info("Обновлен пользователь id={}", id);
        return result;
    }

    @Override
    public UserDto findUserById(int id) {
        try {
            return UserMapper.toUserDto(userDao.findById(id).get());
        } catch (Exception e) {
            throw new NotFoundException("Такой пользователь не найден");
        }
    }

    @Override
    public void deleteUserById(int id) {
        userDao.deleteById(id);
        log.info("Удален пользователь id={}", id);
    }

    @Override
    public boolean isExist(int id) {
        return findUserById(id) != null;
    }
}
