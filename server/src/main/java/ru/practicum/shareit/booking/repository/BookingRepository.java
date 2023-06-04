package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker(User booker, Pageable pageable);

    List<Booking> findByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                       BookingStatus status, LocalDateTime end);

    Page<Booking> findByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndEndBefore(User booker, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerAndStartAfter(User booker, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerAndStatusEquals(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> findByItemOwner(User owner, Pageable pageable);

    Page<Booking> findByItemOwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerAndEndBefore(User owner, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItemOwnerAndStartAfter(User owner, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItemOwnerAndStatusEquals(User owner, BookingStatus status, Pageable pageable);

    List<Booking> findTop1BookingByItemIdAndEndIsBeforeAndStatusIs(
            Long itemId, LocalDateTime end, BookingStatus status, Sort sort);

    List<Booking> findTop1BookingByItemIdAndEndIsAfterAndStatusIs(
            Long itemId, LocalDateTime end, BookingStatus status, Sort sort);
}