package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest mappedItemRequest = new ItemRequest();
        mappedItemRequest.setId(itemRequestDto.getId());
        mappedItemRequest.setDescription(itemRequestDto.getDescription());
        return mappedItemRequest;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto mappedItemRequestDto = new ItemRequestDto();
        mappedItemRequestDto.setId(itemRequest.getId());
        mappedItemRequestDto.setDescription(itemRequest.getDescription());
        mappedItemRequestDto.setCreated(itemRequest.getCreated());
        mappedItemRequestDto.setItems(new ArrayList<>());
        return mappedItemRequestDto;
    }
}
