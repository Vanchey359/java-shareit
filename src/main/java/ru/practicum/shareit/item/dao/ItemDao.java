package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    Item createItem(Long ownerId, Item item);

    Item updateItem(Long itemId, Long ownerId, ItemDto itemDto);

    Item getItemById(Long ownerId, Long itemId);

    List<Item> getAllItems(Long ownerId);

    List<Item> searchItems(Long ownerId, String text);
}
