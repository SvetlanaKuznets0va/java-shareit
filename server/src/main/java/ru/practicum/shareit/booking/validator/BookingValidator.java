package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidator {
    public static void validateBookingDto(BookingDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start) || end.isBefore(LocalDateTime.now()) || start.isBefore(LocalDateTime.now())
        || start.isEqual(end)) {
            throw new ValidationException("Не корретная информация о датах");
        }
    }
}
