package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class UserDaoImpl implements UserDao{
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
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList(userStorage.values());
        return users;
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (userStorage.containsKey(id)) {
            User u = userStorage.get(id);
            String oldEmail = u.getEmail();
            switch (checkUser(user)) {
                case "Full" :
                    userStorage.put(id, user);
                    u = userStorage.get(id);
                    emails.remove(oldEmail);
                    emails.add(user.getEmail());
                    break;
                case "FillName" :
                    u.setName(user.getName());
                    userStorage.put(id, u);
                    break;
                case "FillEmail" :
                    u.setEmail(user.getEmail());
                    userStorage.put(id, u);
                    emails.remove(oldEmail);
                    emails.add(u.getEmail());
                    break;
                default :
                    break;

            }
            return u;
        }
        return null;
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

    private String checkUser(User user) {
        if (emails.contains(user.getEmail()) && !userStorage.get(user.getId()).getEmail().equals(user.getEmail())) {
            throw new RuntimeException("duplicate email");
        }
        if (user.getEmail() == null && user.getName() != null) {
            return "FillName";
        }
        if (user.getEmail() != null && user.getName() == null) {
            return "FillEmail";
        }
        if (user.getEmail() == null && user.getName() == null) {
            return "Empty";
        }
        return "Full";
    }
}
