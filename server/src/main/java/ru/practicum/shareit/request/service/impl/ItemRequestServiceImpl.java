package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();
    private final ItemMapper itemMapper = new ItemMapper();

    @Transactional
    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtos.forEach(this::setItemsToItemRequestDto);
        return itemRequestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(int from, int size, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        List<ItemRequestDto> itemRequestDtos = itemRequestRepository.findByRequestorIsNot(user,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        itemRequestDtos.forEach(this::setItemsToItemRequestDto);
        return itemRequestDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("request with id:" + requestId + " not found error"));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        setItemsToItemRequestDto(itemRequestDto);
        return itemRequestDto;
    }

    private void setItemsToItemRequestDto(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemRepository.findByRequestId(itemRequestDto.getId())
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList()));
    }
}
