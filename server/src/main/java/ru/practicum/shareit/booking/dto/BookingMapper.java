package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
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

    public Booking toBooking(BookingBriefDto bookingBriefDto) {
        Booking result = new Booking();
        result.setStart(bookingBriefDto.getStart());
        result.setEnd(bookingBriefDto.getEnd());

        return result;
    }
}