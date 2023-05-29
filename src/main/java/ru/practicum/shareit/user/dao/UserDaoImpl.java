package ru.practicum.shareit.user.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDaoImpl implements UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private Map<Integer, User> userStorage = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    private static int id = 0;

    @Override
    public User addUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new RuntimeException("duplicate email");
        }
        user.setId(++id);
        userStorage.put(user.getId(), user);
        emails.add(user.getEmail());
        log.info("User with id: " + id + " added.");
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
        log.info("User with id: " + user.getId() + " updated.");
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
            log.info("User with id: " + id + " deleted.");
        }
    }

    @Override
    public Set<String> getEmails() {
        return emails;
    }
}
