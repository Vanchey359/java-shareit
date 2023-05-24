package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        ItemDto mappedItem = new ItemDto();
        mappedItem.setId(item.getId());
        mappedItem.setName(item.getName());
        mappedItem.setDescription(item.getDescription());
        mappedItem.setAvailable(item.getAvailable());
        mappedItem.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return mappedItem;
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item mappedItem = new Item();
        mappedItem.setId(itemDto.getId());
        mappedItem.setName(itemDto.getName());
        mappedItem.setDescription(itemDto.getDescription());
        mappedItem.setAvailable(itemDto.getAvailable());
        mappedItem.setOwner(owner);
        return mappedItem;
    }

    public static List<ItemDto> toDtos(List<Item> items) {
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto itemDto = toDto(item);
            result.add(itemDto);
        }
        return result;
    }
}