package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User createUser(User user);

    User getUserById(Long id);

    List<User> getAllUsers();

    User updateUser(Long id, User updatedUser);

    void removeUser(Long id);
}
