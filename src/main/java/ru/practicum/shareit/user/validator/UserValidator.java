package ru.practicum.shareit.user.validator;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

public class UserValidator {
    public static void validateUser(UserDto userDto) {
        String email = userDto.getEmail();
        if (email == null || !StringUtils.hasText(userDto.getEmail()) || !userDto.getEmail().contains("@")) {
            throw new ValidationException("email fail");
        }
    }
}

