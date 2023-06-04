package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_HEADER_ID = "X-Sharer-User-Id";
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @PostMapping
    public BookingDto create(@RequestBody BookingBriefDto bookingBriefDto,
                             @RequestHeader(USER_HEADER_ID) Long userId) {
        return bookingService.create(bookingBriefDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestHeader(USER_HEADER_ID) Long userId,
                              @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(USER_HEADER_ID) Long userId,
                                          @RequestParam(defaultValue = "ALL") BookingState state,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        Pageable pageRequest = PageRequest.of(from / size, size, sort);
        return bookingService.getAllByOwner(userId, state, pageRequest);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(USER_HEADER_ID) Long userId,
                                         @RequestParam(defaultValue = "ALL") BookingState state,
                                         @RequestParam(defaultValue = "0") int from,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageRequest = PageRequest.of(from / size, size, sort);
        return bookingService.getAllByUser(userId, state, pageRequest);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable Long bookingId, @RequestHeader(USER_HEADER_ID) Long userId) {
        return bookingService.getById(bookingId, userId);
    }
}