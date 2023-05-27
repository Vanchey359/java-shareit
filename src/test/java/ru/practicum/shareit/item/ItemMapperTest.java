package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;
    private final ItemMapper itemMapper = new ItemMapper();

    @BeforeEach
    private void beforeEach() {
        item = new Item(1L, "Item", "Description", true, null, null);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Dto item");
        itemDto.setAvailable(true);
    }

    @Test
    public void toItemDtoTest() {
        ItemDto dto = itemMapper.toDto(item);

        assertEquals(dto.getId(), item.getId());
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
    }

    @Test
    public void toItemModelTest() {
        Item newItem = itemMapper.toItem(itemDto, new User());

        assertEquals(newItem.getId(), itemDto.getId());
        assertEquals(newItem.getName(), itemDto.getName());
        assertEquals(newItem.getDescription(), itemDto.getDescription());
    }

    @Test
    public void toItemDtoListTest() {
        List<Item> items = List.of(item);
        List<ItemDto> itemDtoList = itemMapper.toDtos(items);

        assertFalse(itemDtoList.isEmpty());
        assertEquals(items.get(0).getId(), itemDtoList.get(0).getId());
        assertEquals(items.get(0).getName(), itemDtoList.get(0).getName());
        assertEquals(items.get(0).getDescription(), itemDtoList.get(0).getDescription());
    }
}
