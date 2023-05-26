package ru.practicum.shareit.request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    private void beforeEach() {

        user = new User(1L, "Ivan", "Averin@yandex.ru");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.parse("2023-10-01T19:34:50.63"));
    }

    @Test
    public void createTest() {
        ItemRequestDto inputDto = new ItemRequestDto();
        inputDto.setId(1L);
        inputDto.setDescription(itemRequest.getDescription());

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto responseDto = itemRequestService.create(1L, inputDto);

        assertEquals(1L, responseDto.getId());
        assertEquals(inputDto.getDescription(), responseDto.getDescription());
    }

    @Test
    void getAllByUserTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository
                .findByRequestorIdOrderByCreatedAsc(any(Long.class)))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDto> result = itemRequestService.getAllByUserId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.findByRequestorNotLikeOrderByCreatedAsc(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        List<ItemRequestDto> result = itemRequestService.getAll(0, 10, 1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        when(itemRequestRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(itemRequest));

        when(itemRepository.findByRequestId(any(Long.class)))
                .thenReturn(new ArrayList<>());


        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertEquals(1L, result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }
}
