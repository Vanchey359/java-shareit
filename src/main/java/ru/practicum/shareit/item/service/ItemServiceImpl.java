package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));

        Item item = ItemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ObjectNotFoundException("request with id:" + itemDto.getRequestId() + " not found error"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new WrongUserException("wrong user:" + item.getOwner().getId() + " is not an owner of " + itemDto);
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto findById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));
        ItemDto itemDto = ItemMapper.toDto(item);

        itemDto.setComments(commentRepository.findByItemId(itemId)
                .stream().map(CommentMapper::toDto).collect(Collectors.toList()));

        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }

        List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

        itemDto.setLastBooking(lastBooking.isEmpty() ? null : bookingMapper.toBriefDto(lastBooking.get(0)));

        List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

        itemDto.setNextBooking(nextBooking.isEmpty() ? null : bookingMapper.toBriefDto(nextBooking.get(0)));

        if (itemDto.getLastBooking() == null && itemDto.getNextBooking() != null) {
            itemDto.setLastBooking(itemDto.getNextBooking());
            itemDto.setNextBooking(null);
        }

        return itemDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getAllByUserId(Long userId, int from, int size) {
        List<ItemDto> itemsDto = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size))
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        if (itemsDto.isEmpty()) {
            return new ArrayList<>();
        }


        for (ItemDto itemDto : itemsDto) {
            itemDto.setComments(commentRepository.findByItemId(itemDto.getId())
                    .stream().map(CommentMapper::toDto).collect(Collectors.toList()));

            List<Booking> lastBooking = bookingRepository.findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"));

            itemDto.setLastBooking(lastBooking.isEmpty() ? new BookingBriefDto() : bookingMapper.toBriefDto(lastBooking.get(0)));

            List<Booking> nextBooking = bookingRepository.findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
                    itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"));

            itemDto.setNextBooking(nextBooking.isEmpty() ? new BookingBriefDto() : bookingMapper.toBriefDto(nextBooking.get(0)));
        }

        itemsDto.sort(Comparator.comparing(o -> o.getLastBooking().getStart(), Comparator.nullsLast(Comparator.reverseOrder())));

        for (ItemDto itemDto : itemsDto) {
            if (itemDto.getLastBooking().getBookerId() == null) {
                itemDto.setLastBooking(null);
            }
            if (itemDto.getNextBooking().getBookerId() == null) {
                itemDto.setNextBooking(null);
            }
        }

        return itemsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Item> findByRequest(String request, int from, int size) {
        if (request == null || request.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> result = new ArrayList<>();
        request = request.toLowerCase();

        List<Item> itemsList = itemRepository.findAll(PageRequest.of(from, size))
                .stream()
                .collect(Collectors.toList());

        for (Item item : itemsList) {
            String name = item.getName().toLowerCase();
            String description = item.getDescription().toLowerCase();

            if (item.getAvailable().equals(true) && (name.contains(request) || description.contains(request))) {
                result.add(item);
            }
        }

        return result;
    }

    @Transactional
    @Override
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + itemId + " not found error"));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, BookingStatus.APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Wrong item for leaving comment");
        }
        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentMapper.toDto(comment);
    }
}
