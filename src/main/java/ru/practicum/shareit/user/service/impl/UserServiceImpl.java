package ru.practicum.shareit.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Qualifier("inMemoryStorage")
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userDao.createUser(validate(userDto, true));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userDao.getAllUsers();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        return usersDto;
    }
    @Override
    public UserDto updateUser(Long userId, UserDto updatedUser) {
        if (!checkUser(userId)) {
            throw new NotFoundException("User not found");
        }
        User user1 = userDao.getUserById(userId);
        if (updatedUser.getEmail() != null && updatedUser.getEmail().equals(user1.getEmail())) {
            User user2 = userDao.updateUser(userId, UserMapper.toUser(updatedUser));
            return UserMapper.toUserDto(user2);
        } else {
            User user = userDao.updateUser(userId, validate(updatedUser, false));
            return UserMapper.toUserDto(user);
        }
    }

    @Override
    public void removeUser(Long userId) {
        if (!checkUser(userId)) {
            throw new NotFoundException("User not found");
        }
        userDao.removeUser(userId);
    }
    @Override
    public boolean checkUser(Long userId) {
        return userDao.getAllUsers().stream().anyMatch(user -> user.getId() == userId);
    }

    private boolean checkEmail(String email) {
        return userDao.getAllUsers().stream().anyMatch(user ->user.getEmail().equals(email));
    }

    private User validate(UserDto userDto, boolean isNew) {
        String email = userDto.getEmail();
        if (isNew && email == null) {
            throw new ValidationException("Email not written");
        }
        if (checkEmail(email)) {
            throw new ConflictException("This email is already taken");
        }
        return UserMapper.toUser(userDto);
    }
}
