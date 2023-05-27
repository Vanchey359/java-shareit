package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    private Booking booking;
    private BookingBriefDto bookingBriefDto;
    private final BookingMapper bookingMapper = new BookingMapper();

    @BeforeEach
    private void beforeEach() {
        booking = new Booking(1L,
                LocalDateTime.parse("2023-10-01T19:34:50.63"),
                LocalDateTime.parse("2023-10-02T19:34:50.63"),
                new Item(), new User(), null);

        bookingBriefDto = new BookingBriefDto();
        bookingBriefDto.setId(1L);
        bookingBriefDto.setStart(LocalDateTime.parse("2023-10-01T19:34:50.63"));
        bookingBriefDto.setEnd(LocalDateTime.parse("2023-10-02T19:34:50.63"));
    }

    @Test
    public void toBookingDtoTest() {
        BookingDto dto = bookingMapper.toDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void bookingBriefDtoTest() {
        BookingBriefDto dto = bookingMapper.toBriefDto(booking);

        assertEquals(dto.getId(), booking.getId());
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
    }

    @Test
    public void toBookingTest() {
        Booking newBooking = bookingMapper.toBooking(bookingBriefDto);

        assertEquals(newBooking.getStart(), bookingBriefDto.getStart());
        assertEquals(newBooking.getEnd(), bookingBriefDto.getEnd());
    }
}
