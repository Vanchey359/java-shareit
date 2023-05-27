package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper = new BookingMapper();
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Transactional
    @Override
    public BookingDto create(BookingBriefDto bookingBriefDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        Item item = itemRepository.findById(bookingBriefDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException("item with id:" + bookingBriefDto.getItemId() + " not found error"));

        if (item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("user:" + userId + " can't book it's own item");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("item:" + item.getId() + " is not available");
        }
        Booking booking = bookingMapper.toBooking(bookingBriefDto);
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new BadRequestException("wrong booking start and end periods");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Transactional
    @Override
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Booking with id:" + bookingId + " not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException("user id:" + userId + " is not an owner of item:" + booking.getItem().getId());
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new BadRequestException("Booking is already approved or rejected");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByOwner(Long userId, BookingState state, Pageable pageRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("user with id:" + userId + " not found error"));
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings.addAll(bookingRepository.findByItemOwner(user, pageRequest).toList());
                break;

            case FUTURE:
                bookings.addAll(bookingRepository.findByItemOwnerAndStartAfter(user, LocalDateTime.now(), pageRequest).toList());
                break;

            case CURRENT:
                bookings.addAll(bookingRepository.findByItemOwnerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest).toList());
                break;

            case PAST:
                bookings.addAll(bookingRepository.findByItemOwnerAndEndBefore(user,
                        LocalDateTime.now(), pageRequest).toList());
                break;

            case WAITING:
                bookings.addAll(bookingRepository.findByItemOwnerAndStatusEquals(user, BookingStatus.WAITING, pageRequest).toList());
                break;

            case REJECTED:
                bookings.addAll(bookingRepository.findByItemOwnerAndStatusEquals(user, BookingStatus.REJECTED, pageRequest).toList());
                break;

            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllByUser(Long userId, BookingState state, Pageable pageRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Unable to find bookings - " +
                        "user id not found " + userId));
        List<Booking> bookingDtoList = new ArrayList<>();
        switch (state) {
            case ALL:
                bookingDtoList.addAll(bookingRepository.findByBooker(user, pageRequest).toList());
                break;

            case REJECTED:
                bookingDtoList.addAll(bookingRepository.findByBookerAndStatusEquals(user, BookingStatus.REJECTED, pageRequest).toList());
                break;

            case CURRENT:
                bookingDtoList.addAll(bookingRepository.findByBookerAndStartBeforeAndEndAfter(user,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest).toList());
                break;

            case PAST:
                bookingDtoList.addAll(bookingRepository.findByBookerAndEndBefore(user,
                        LocalDateTime.now(), pageRequest).toList());
                break;

            case WAITING:
                bookingDtoList.addAll(bookingRepository.findByBookerAndStatusEquals(user, BookingStatus.WAITING, pageRequest).toList());
                break;

            case FUTURE:
                bookingDtoList.addAll(bookingRepository.findByBookerAndStartAfter(user, LocalDateTime.now(), pageRequest).toList());
                break;

            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return bookingDtoList.stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("No booking with id:" + bookingId));
        if (!userId.equals(booking.getBooker().getId()) && !userId.equals(booking.getItem().getOwner().getId())) {
            throw new ObjectNotFoundException("access denied");
        }

        return bookingMapper.toDto(booking);
    }
}
