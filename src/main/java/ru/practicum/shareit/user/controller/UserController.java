package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toDto(userService.create(user));
    }

    @GetMapping("/{userId}")
    public UserDto findById(@NotNull @PathVariable Long userId) {
        return UserMapper.toDto(userService.findById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return UserMapper.toDtos(userService.getAll());
    }

    @PatchMapping("/{userId}")
    public UserDto update(@NotNull @PathVariable Long userId,
                          @RequestBody UserDto userDto) {
        return UserMapper.toDto(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void delete(@NotNull @PathVariable Long userId) {
        userService.delete(userId);
    }
}
