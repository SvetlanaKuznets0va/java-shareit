package ru.practicum.shareit.user.mapper;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static User combineUserWithUserDto(User user, UserDto userDto) {
        return new User(
                user.getId(),
                StringUtils.hasText(userDto.getEmail()) ? userDto.getEmail() : user.getEmail(),
                StringUtils.hasText(userDto.getName()) ? userDto.getName() : user.getName());
    }

    public static User toUserWithoutId(UserDto userDto) {
        return new User(userDto.getEmail(), userDto.getName());
    }

    public static UserDto toUserDto(User user) {
        return user == null ? null : new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User toUser(UserDto userDto) {
        return userDto == null ? null : new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }
}
