package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDaoImpl implements UserDao{
    private Map<Integer, User> userStorage = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    private static int id = 0;

    @Override
    public User addUser(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            throw new RuntimeException("duplicate email");
        }
        User user = new User(++id, userDto.getEmail(), userDto.getName());
        userStorage.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList(userStorage.values());
        return users;
    }

    @Override
    public User updateUser(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(int id) {
        if (userStorage.containsKey(id)) {
            return userStorage.get(id);
        }
        return null;
    }

    @Override
    public void deleteUserById(int id) {
        if (userStorage.containsKey(id)) {
            emails.remove(userStorage.get(id).getEmail());
            userStorage.remove(id);
        }
    }

    @Override
    public Set<String> getEmails() {
        return emails;
    }
}
