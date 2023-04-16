package ru.practicum.shareit.user.service.impl;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Created user: {}", userDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userDao.getUserById(userId);
        log.info("User with id {} found", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userDao.getAllUsers();
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(UserMapper.toUserDto(user));
        }
        log.info("All users found: {}", usersDto.size());
        return usersDto;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto updatedUser) {
        if (!checkUser(userId)) {
            log.error("updateUser error: User not found");
            throw new NotFoundException("User not found");
        }
        User userById = userDao.getUserById(userId);
        if (updatedUser.getEmail() != null && updatedUser.getEmail().equals(userById.getEmail())) {
            User userUp = userDao.updateUser(userId, UserMapper.toUser(updatedUser));
            log.info("User with id: {} was updated", userUp.getId());
            return UserMapper.toUserDto(userUp);
        }
        User user = userDao.updateUser(userId, validate(updatedUser, false));
        log.info("User with id: {} was updated", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void removeUser(Long userId) {
        if (!checkUser(userId)) {
            log.error("removeUser error: User not found");
            throw new NotFoundException("User not found");
        }
        userDao.removeUser(userId);
        log.info("User with id: {} removed", userId);
    }

    @Override
    public boolean checkUser(Long userId) {
        return userDao.getAllUsers().stream().anyMatch(user -> user.getId().equals(userId));
    }

    private boolean checkEmail(String email) {
        return userDao.getAllUsers().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    private User validate(UserDto userDto, boolean isNew) {
        String email = userDto.getEmail();
        if (isNew && email == null) {
            log.error("Validation error: Email not written");
            throw new ValidationException("Email not written");
        }
        if (checkEmail(email)) {
            log.error("Validation error: This email is already taken");
            throw new ConflictException("This email is already taken");
        }
        return UserMapper.toUser(userDto);
    }
}
