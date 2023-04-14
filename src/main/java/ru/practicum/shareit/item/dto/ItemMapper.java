package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

@Component
public class ItemMapper {

    private final UserDao userDao;

    @Autowired
    public ItemMapper(UserDao userDao) {
        this.userDao = userDao;
    }
    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public Item toItem(Long ownerId, ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                userDao.getUserById(ownerId),
                null
        );
    }
}
