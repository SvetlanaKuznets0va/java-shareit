package ru.practicum.shareit.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.constants.Constatnts;
import ru.practicum.shareit.constants.State;
import ru.practicum.shareit.constants.Status;
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
    private final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

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
            log.info("Владелец id={}, попытка забронировать свою вещь", userId);
            throw new NotFoundException("Нельзя бронировать свои вещи");
        }
        User user = getUser(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        BookingDtoResp result = BookingMapper.toBookingDtoResp(bookingDao.save(booking));
        log.info("Пользователь id={}, добавил бронирование id={}", userId, result.getId());
        return result;
    }

    @Override
    public BookingDtoResp approve(int ownerId, int bookingId, boolean approved) {
        Booking booking = bookingDao.findById(bookingId).orElseThrow(() -> new NotFoundException("Запись не найдена"));
        if (booking.getItem().getOwnerId() != ownerId) {
            log.info("Пользователь id={}, не владелец вещи для брони id={}", ownerId, bookingId);
            throw new NotFoundException("Владелец не найден");
        }
        if (booking.getStatus() == Status.APPROVED) {
            log.info("Владелец id={}, попытка повторного утверждения", ownerId);
            throw new ValidationException("Вещь уже утверждена");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        BookingDtoResp result = BookingMapper.toBookingDtoResp(bookingDao.save(booking));
        log.info("Владелец id={}, для брони id={} установил статус {}", ownerId, bookingId, result.getStatus());
        return result;
    }

    @Override
    public BookingDtoResp getByBookingId(int userId, int bookingId) {
        return BookingMapper.toBookingDtoResp(getBooking(bookingId, userId));
    }

    @Override
    public List<BookingDtoResp> getAllForUser(Integer from, Integer size, int userId, String state) {
        boolean isPagebale = Constatnts.checkPagination(from, size);
        getUser(userId);
        List<Booking> result = null;

        State stateStatus = checkState(state);
        Pageable pagebale = isPagebale ? PageRequest.of(from > 0 ? from / size : 0, size) : null;
        switch (stateStatus) {
            case ALL:
                result = isPagebale ? bookingDao.findAllByBooker(userId, pagebale) :
                        bookingDao.findAllByBooker(userId);
                break;
            case FUTURE:
                result = isPagebale ? bookingDao.findFutureByBooker(userId, pagebale) :
                        bookingDao.findFutureByBooker(userId);
                break;
            case PAST:
                result = isPagebale ? bookingDao.findPastByBooker(userId, pagebale) :
                        bookingDao.findPastByBooker(userId);
                break;
            case CURRENT:
                result = isPagebale ? bookingDao.findCurrentByBooker(userId, pagebale) :
                        bookingDao.findCurrentByBooker(userId);
                break;
            case WAITING:
                result = isPagebale ? bookingDao.findWaitingOrRejectedByBooker(userId, Status.WAITING, pagebale) :
                        bookingDao.findWaitingOrRejectedByBooker(userId, Status.WAITING);
                break;
            case REJECTED:
                result = isPagebale ? bookingDao.findWaitingOrRejectedByBooker(userId, Status.REJECTED, pagebale) :
                        bookingDao.findWaitingOrRejectedByBooker(userId, Status.REJECTED);
                break;
        }

        return result.stream()
                .map(BookingMapper::toBookingDtoResp)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResp> getAllForOwner(Integer from, Integer size, int ownerId, String state) {
        boolean isPagebale = Constatnts.checkPagination(from, size);
        getUser(ownerId);
        List<Booking> result = null;

        State stateStatus = checkState(state);
        Pageable pagebale = isPagebale ? PageRequest.of(from > 0 ? from / size : 0, size) : null;
        switch (stateStatus) {
            case ALL:
                result = isPagebale ? bookingDao.findAllByOwner(ownerId, pagebale) :
                        bookingDao.findAllByOwner(ownerId);
                break;
            case FUTURE:
                result = isPagebale ? bookingDao.findFutureByOwner(ownerId, pagebale) :
                        bookingDao.findFutureByOwner(ownerId);
                break;
            case PAST:
                result = isPagebale ? bookingDao.findPastByOwner(ownerId, pagebale) :
                        bookingDao.findPastByOwner(ownerId);
                break;
            case CURRENT:
                result = isPagebale ? bookingDao.findCurrentByOwner(ownerId, pagebale) :
                        bookingDao.findCurrentByOwner(ownerId);
                break;
            case WAITING:
                result = isPagebale ? bookingDao.findWaitingOrRejectedByOwner(ownerId, Status.WAITING, pagebale) :
                        bookingDao.findWaitingOrRejectedByOwner(ownerId, Status.WAITING);
                break;
            case REJECTED:
                result = isPagebale ? bookingDao.findWaitingOrRejectedByOwner(ownerId, Status.REJECTED, pagebale) :
                        bookingDao.findWaitingOrRejectedByOwner(ownerId, Status.REJECTED);
                break;
        }

        return result.stream()
                .map(BookingMapper::toBookingDtoResp)
                .collect(Collectors.toList());
    }

    private State checkState(String state) {
        try {
            return State.valueOf(State.class, state);
        } catch (IllegalArgumentException e) {
            log.warn("Не поддерживаемый статус: {}", state);
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private Booking getBooking(int bookingId, int userId) {
        return bookingDao.findBookingByIdAndBookerId(bookingId, userId).orElseThrow(() -> new NotFoundException("Запись не найдена"));
    }

    private Item getItem(int itemId) {
        Item item = itemDao.findById(itemId).orElseThrow(() -> new NotFoundException("Такая вещь не найдена"));
        if (!item.isAvailable()) {
            throw new ValidationException("Вещь не доступна");
        }
        return item;
    }

    private User getUser(int userId) {
        return userDao.findById(userId).orElseThrow(() -> new NotFoundException("Такой пользователь не найден"));
    }
}
