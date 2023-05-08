package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingBriefDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        BookingDto result = new BookingDto();
        result.setId(booking.getId());
        result.setStart(booking.getStart());
        result.setEnd(booking.getEnd());
        result.setStatus(booking.getStatus());
        result.setBooker(booking.getBooker());
        result.setItem(booking.getItem());
        return result;
    }

    public BookingBriefDto toBriefDto(Booking booking) {
        BookingBriefDto result = new BookingBriefDto();
        result.setId(booking.getId());
        result.setStart(booking.getStart());
        result.setEnd(booking.getEnd());
        result.setItemId(booking.getItem().getId());
        result.setBookerId(booking.getBooker().getId());
        return result;
    }

    public Booking toBooking(BookingDto bookingDto) {
        Booking result = new Booking();
        result.setId(bookingDto.getId());
        result.setStart(bookingDto.getStart());
        result.setEnd(bookingDto.getEnd());
        result.setBooker(bookingDto.getBooker());
        result.setStatus(bookingDto.getStatus());
        result.setItem(bookingDto.getItem());
        return result;
    }

    public Booking toBooking(BookingBriefDto bookingBriefDto) {
        Booking result = new Booking();
        result.setStart(bookingBriefDto.getStart());
        result.setEnd(bookingBriefDto.getEnd());

        return result;
    }
}