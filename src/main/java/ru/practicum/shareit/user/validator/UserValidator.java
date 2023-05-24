package ru.practicum.shareit.user.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

@Component
public class UserValidator {
    private UserDao userDao;

    public UserValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    public  void validateUser(User user) {
        String email = user.getEmail();

        if (email == null || !StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new ValidationException("email fail");
        }
    }
}

