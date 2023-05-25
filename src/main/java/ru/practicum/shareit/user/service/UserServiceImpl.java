package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User addUser(UserDto userDto) {
        UserValidator.validateUser(userDto);
        return userDao.addUser(userDto);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public User updateUser(UserDto userDto, int id) {
        User userBefore = findUserById(id);
        if (userBefore == null) {
            return null;
        }
        String updEmail = checkEmail(userBefore, userDto);
        User userAfter = UserMapper.CombineUserWithUserDto(userBefore, userDto);
        userAfter.setEmail(updEmail);

        return userDao.updateUser(userAfter);
    }

    @Override
    public User findUserById(int id) {
        return userDao.findUserById(id);
    }

    @Override
    public void deleteUserById(int id) {
        userDao.deleteUserById(id);
    }

    private String checkEmail(User user, UserDto userDto) {
        if (userDao.getEmails().contains(userDto.getEmail()) && !user.getEmail().equals(userDto.getEmail())) {
            throw new RuntimeException("duplicate email");
        }
        if (user.getEmail().equals(userDto.getEmail()) || userDto.getEmail() == null) {
            return user.getEmail();
        }
        if (userDto.getEmail() != null) {
            userDao.getEmails().remove(user.getEmail());
            userDao.getEmails().add(userDto.getEmail());
            return userDto.getEmail();
        }
        return null;
    }
}
