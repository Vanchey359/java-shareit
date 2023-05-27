package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_HEADER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER_ID) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(USER_HEADER_ID) Long userId) {
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(defaultValue = "10") @PositiveOrZero int size,
                                       @RequestHeader(USER_HEADER_ID) Long userId) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Error - wrong searching parameters!");
        }
        return itemRequestService.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable Long requestId, @RequestHeader(USER_HEADER_ID) Long userId) {
        return itemRequestService.getById(requestId, userId);
    }
}
