package ru.practicum.shareit.item.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Qualifier("InMemoryItemDao")
    private final ItemDao itemDao;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, ItemMapper itemMapper, UserService userService) {
        this.itemDao = itemDao;
        this.itemMapper = itemMapper;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        checkOwnerById(ownerId);
        Item item = itemDao.createItem(ownerId, itemMapper.toItem(ownerId, itemDto));
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto) {
        checkOwnerById(ownerId);
        checkItemById(itemId, ownerId);
        Item item = itemDao.updateItem(itemId, ownerId, itemDto);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long ownerId, Long itemId) {
        checkOwnerById(ownerId);
        Item item = itemDao.getItemById(ownerId, itemId);
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        checkOwnerById(ownerId);
        List<Item> items = itemDao.getAllItems(ownerId);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(Long ownerId, String text) {
        List<Item> items = itemDao.searchItems(ownerId, text);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    private void checkOwnerById(Long ownerId) {
        if (!userService.checkUser(ownerId)) {
            throw new NotFoundException("Owner with this id not found");
        }
    }

    private void checkItemById(Long itemId, Long ownerId) {
        if (itemDao.getAllItems(ownerId).stream().noneMatch(item -> item.getId().equals(itemId))) {
            throw new NotFoundException("Item not found");
        }
    }
}
