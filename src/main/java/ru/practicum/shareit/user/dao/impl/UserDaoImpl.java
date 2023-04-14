package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("InMemoryStorage")
public class UserDaoImpl implements UserDao {

    private static Long currentId = 1L;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(currentId);
        users.put(currentId++, user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User user = users.get(id);
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        users.put(id, user);
        return user;
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
    }
}
