package ru.practicum.shareit.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Created user: {}", userDto);
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        log.info("User with id {} found", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = new ArrayList<UserDto>(userService.getAllUsers());
        log.info("All users found: {}", allUsers.size());
        return allUsers;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("User with id: {} was updated", userDto.getId());
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable("userId") Long userId) {
        userService.removeUser(userId);
        log.info("User with id: {} removed", userId);
    }

}
