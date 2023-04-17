package ru.practicum.shareit.item.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("InMemoryItemDao")
public class ItemDaoImpl implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private static Long currentId = 1L;
    private final UserDao userDao;

    @Autowired
    public ItemDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Item createItem(Long ownerId, Item item) {
        item.setId(currentId);
        item.setOwner(userDao.getUserById(ownerId));
        items.put(currentId++, item);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, Long ownerId, ItemDto itemDto) {
        Item item = items.get(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item getItemById(Long ownerId, Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAllItems(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(Long ownerId, String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}
