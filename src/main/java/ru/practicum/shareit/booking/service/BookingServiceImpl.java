package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.constants.State;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.exception.InvalidStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private BookingDao bookingDao;
    private ItemDao itemDao;
    private UserDao userDao;

    public BookingServiceImpl(BookingDao bookingDao, ItemDao itemService, UserDao userService) {
        this.bookingDao = bookingDao;
        this.itemDao = itemService;
        this.userDao = userService;
    }

    @Override
    public BookingDtoResp add(BookingDto bookingDto, int userId) {
        BookingValidator.validateBookingDto(bookingDto);
        Item item = getItem(bookingDto.getItemId());
        if (item.getOwnerId() == userId) {
            throw new NotFoundException("Нельзя бронировать свои вещи");
        }
        User user = getUser(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        return BookingMapper.toBookingDtoResp(bookingDao.save(booking));
    }

    @Override
    public BookingDtoResp approve(int ownerId, int bookingId, boolean approved) {
        Booking booking = bookingDao.findById(bookingId).orElseThrow(() -> new NotFoundException("Запись не найдена"));
        if (booking.getItem().getOwnerId() != ownerId) {
            throw new NotFoundException("Владелец не найден");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Вещь уже утверждена");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingDtoResp(bookingDao.save(booking));
    }

    @Override
    public BookingDtoResp getByBookingId(int userId, int bookingId) {
        return BookingMapper.toBookingDtoResp(getBooking(bookingId, userId));
    }

    @Override
    public List<BookingDtoResp> getAllForUser(int userId, String state) {
        getUser(userId);
        List<Booking> result = null;

        State stateStatus = checkState(state);

        switch (stateStatus) {
            case ALL:
                result = bookingDao.findAllByBooker(userId);
                break;
            case FUTURE:
                result = bookingDao.findFutureByBooker(userId);
                break;
            case PAST:
                result = bookingDao.findPastByBooker(userId);
                break;
            case CURRENT:
                result = bookingDao.findCurrentByBooker(userId);
                break;
            case WAITING:
                result = bookingDao.findWaitingOrRejectedByBooker(userId, Status.WAITING);
                break;
            case REJECTED:
                result = bookingDao.findWaitingOrRejectedByBooker(userId, Status.REJECTED);
                break;
        }

        return result.stream()
                .map(b -> BookingMapper.toBookingDtoResp(b))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResp> getAllForOwner(int ownerId, String state) {
        getUser(ownerId);
        List<Booking> result = null;

        State stateStatus = checkState(state);

        switch (stateStatus) {
            case ALL:
                result = bookingDao.findAllByOwner(ownerId);
                break;
            case FUTURE:
                result = bookingDao.findFutureByOwner(ownerId);
                break;
            case PAST:
                result = bookingDao.findPastByOwner(ownerId);
                break;
            case CURRENT:
                result = bookingDao.findCurrentByOwner(ownerId);
                break;
            case WAITING:
                result = bookingDao.findWaitingOrRejectedByOwner(ownerId, Status.WAITING);
                break;
            case REJECTED:
                result = bookingDao.findWaitingOrRejectedByOwner(ownerId, Status.REJECTED);
                break;
        }

        return result.stream()
                .map(b -> BookingMapper.toBookingDtoResp(b))
                .collect(Collectors.toList());
    }

    private State checkState(String state) {
        try {
            return State.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking getBooking(int bookingId, int userId) {
        return bookingDao.findBookingByIdAndBookerId(bookingId, userId).orElseThrow(() -> new NotFoundException("Запись не найдена"));
    }

    private Item getItem(int itemId) {
        Item item = itemDao.findById(itemId).orElseThrow(() -> new NotFoundException("Такая вещь не найдена"));
        if (item.isAvailable() == false) {
            throw new ValidationException("Вещь не доступна");
        }
        return item;
    }

    private User getUser(int userId) {
        return userDao.findById(userId).orElseThrow(() -> new NotFoundException("Такой пользователь не найден"));
    }
}
