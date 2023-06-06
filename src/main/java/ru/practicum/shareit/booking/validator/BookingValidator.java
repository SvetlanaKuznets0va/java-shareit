package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidator {
    public static void validateBookingDto(BookingDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (bookingDto.getItemId() == null) {
            throw new ValidationException("Не указана вещь для бронирования");
        }
        if (start == null) {
            throw new ValidationException("Не указана дата начала бронирования");
        }
        if (end == null) {
            throw new ValidationException("Не указана дата окончания бронирования");
        }
        if (end.isBefore(start) || end.isBefore(LocalDateTime.now()) || start.isBefore(LocalDateTime.now())
        || start.isEqual(end)) {
            throw new ValidationException("Не корретная информация о датах");
        }
    }
}
