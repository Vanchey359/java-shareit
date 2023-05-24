package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String userHeaderId = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(userHeaderId) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(userHeaderId) Long userId) {
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestHeader(userHeaderId) Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Error - wrong searching parameters!");
        }
        return itemRequestService.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId, @RequestHeader(userHeaderId) Long userId) {
        return itemRequestService.getById(requestId, userId);
    }
}
