package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private final UserMapper userMapper = new UserMapper();

    @BeforeEach
    private void beforeEach() {
        user = new User(1L, "Ivan", "Averin@yandex.ru");
    }

    @Test
    void createTest() {
        User savedUser = new User();
        savedUser.setName(user.getName());
        savedUser.setEmail(user.getEmail());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User newUser = userService.create(savedUser);

        assertEquals(1, newUser.getId());
        assertEquals(savedUser.getName(), newUser.getName());
        assertEquals(savedUser.getEmail(), newUser.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateTest() {
        user.setName("updated name");
        UserDto inputDto = userMapper.toDto(user);

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));

        User userDto = userService.update(1, inputDto);

        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), inputDto.getName());
    }

    @Test
    void findByIdTest() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(user));

        User userDto = userService.findById(1);

        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void deleteByIdTest() {
        userService.delete(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAll();

        assertFalse(users.isEmpty());
        assertEquals(user.getId(), users.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }
}