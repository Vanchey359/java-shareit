package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @NotNull @RequestHeader(USER_HEADER_ID) Long userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @NotNull @PathVariable Long itemId,
                          @NotNull @RequestHeader(USER_HEADER_ID) Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@NotNull @PathVariable Long itemId,
                            @NotNull @RequestHeader(USER_HEADER_ID) Long userId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllByUserId(@NotNull @RequestHeader(USER_HEADER_ID) Long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        checkSearchingParams(from, size);
        return itemService.getAllByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> findByRequest(@RequestParam String text,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        checkSearchingParams(from, size);
        List<Item> foundItems = itemService.findByRequest(text, from, size);
        return ItemMapper.toDtos(foundItems);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable Long itemId, @RequestHeader(USER_HEADER_ID) Long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(itemId, userId, commentDto);
    }

    private void checkSearchingParams(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Error - wrong searching parameters!");
        }
    }
}