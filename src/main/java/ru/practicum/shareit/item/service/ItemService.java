package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto);

    ItemDto getItemById(Long ownerId, Long itemId);

    List<ItemDto> getAllItems(Long ownerId);

    List<ItemDto> searchItems(Long ownerId, String text);
}
