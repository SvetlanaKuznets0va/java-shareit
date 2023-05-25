package ru.practicum.shareit.user.mapper;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static User CombineUserWithUserDto(User user, UserDto userDto) {
        return new User(
                user.getId(),
                StringUtils.hasText(userDto.getEmail()) ? userDto.getEmail() : user.getEmail(),
                StringUtils.hasText(userDto.getName()) ? userDto.getName() : user.getName());
    }
}
