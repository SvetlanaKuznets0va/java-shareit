package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;

import java.util.List;

public interface BookingService {
    BookingDtoResp add(BookingDto bookingDto, int userId);

    BookingDtoResp approve(int ownerId, int bookingId, boolean approved);

    BookingDtoResp getByBookingId(int userId, int bookingId);

    List<BookingDtoResp> getAllForUser(Integer from, Integer size, int userId, String state);

    List<BookingDtoResp> getAllForOwner(Integer from, Integer size, int ownerId, String state);


}
