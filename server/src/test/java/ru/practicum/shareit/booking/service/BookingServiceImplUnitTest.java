package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {
    @Mock
    ItemDao itemDao;
    @Mock
    BookingDao bookingDao;
    @Mock
    UserDao userDao;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime start = LocalDateTime.parse(LocalDateTime.now().plusMinutes(10).format(dtf));
    LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().plusMinutes(50).format(dtf));

    @Test
    void shouldAddBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        BookingDto bookingDto = new BookingDto(0, start, end, 1, null, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(item));
        Mockito.when(bookingDao.save(any())).thenReturn(booking);

        BookingDtoResp result = service.add(bookingDto, 1);

        assertEquals(1, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void shouldThrowNotFoundExceptionIfItemNotFoundInAddBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        BookingDto bookingDto = new BookingDto(0, start, end, 1, null, null);

        Mockito.when(itemDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.add(bookingDto, 1));
    }

    @Test
    void shouldThrowValidationExceptionIfItemNotAvailableInAddBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        BookingDto bookingDto = new BookingDto(0, start, end, 1, null, null);
        Item item = new Item(1, 2, "item", "description", false, null);

        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> service.add(bookingDto, 1));
    }

    @Test
    void shouldThrowNotFoundExceptionIfOwnerEqualBookerInAddBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        BookingDto bookingDto = new BookingDto(0, start, end, 1, null, null);
        Item item = new Item(1, 1, "item", "description", true, null);

        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> service.add(bookingDto, 1));
    }

    @Test
    void shouldThrowNotFoundExceptionIfUserNotFoundInAddBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        BookingDto bookingDto = new BookingDto(0, start, end, 1, null, null);
        Item item = new Item(1, 2, "item", "description", true, null);

        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(item));
        Mockito.when(userDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.add(bookingDto, 1));
    }


    @Test
    void shouldApproveBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Booking bookingApprove = new Booking(1, start, end, item, booker, Status.APPROVED);

        Mockito.when(bookingDao.findById(1)).thenReturn(Optional.of(booking));
        Mockito.when(bookingDao.save(bookingApprove)).thenReturn(bookingApprove);

        BookingDtoResp result = service.approve(2, 1, true);

        assertEquals(1, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    void shouldRejectedBookingInApproveBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Booking bookingApprove = new Booking(1, start, end, item, booker, Status.REJECTED);

        Mockito.when(bookingDao.findById(1)).thenReturn(Optional.of(booking));
        Mockito.when(bookingDao.save(bookingApprove)).thenReturn(bookingApprove);

        BookingDtoResp result = service.approve(2, 1, false);

        assertEquals(1, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(Status.REJECTED, result.getStatus());
    }

    @Test
    void shouldThrowNotFoundExceptionIfBookingNotFoundInApproveBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);

        Mockito.when(bookingDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.approve(1, 1, true));
    }

    @Test
    void shouldThrowNotFoundExceptionIfOwnerNotOwnerInApproveBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);

        Mockito.when(bookingDao.findById(1)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> service.approve(1, 1, true));
    }

    @Test
    void shouldThrowValidationExceptionIfItemAlreadyApprovedInApproveBooking() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.APPROVED);

        Mockito.when(bookingDao.findById(1)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> service.approve(2, 1, true));
    }


    @Test
    void shouldGetByBookingId() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);

        Mockito.when(bookingDao.findBookingByIdAndBookerId(1, 1)).thenReturn(Optional.of(booking));

        BookingDtoResp result = service.getByBookingId(1, 1);

        assertEquals(1, result.getId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(item.getId(), result.getItem().getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void shouldThrowNotFoundExceptionIfBookingIdNotFoundInGetByBookingId() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);

        Mockito.when(bookingDao.findBookingByIdAndBookerId(2, 1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getByBookingId(1, 2));
    }

    @Test
    void shouldThrowNotFoundExceptionIfUserIdNotFoundInGetByBookingId() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);

        Mockito.when(bookingDao.findBookingByIdAndBookerId(1, 3)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.getByBookingId(3, 1));
    }


    @Test
    void shouldGetAllBookingsForUserWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(bookingDao.findAllByBooker(1, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForUser(from, size, 1, "ALL");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetFutureBookingsForUserWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(bookingDao.findFutureByBooker(1, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForUser(from, size, 1, "FUTURE");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetPastBookingsForUserWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start.minusHours(2), end.minusHours(2), item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(bookingDao.findPastByBooker(1, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForUser(from, size, 1, "PAST");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start.minusHours(2), result.get(0).getStart());
        assertEquals(end.minusHours(2), result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetCurrentBookingsForUserWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start.minusHours(2), end.plusHours(2), item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(bookingDao.findCurrentByBooker(1, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForUser(from, size, 1, "CURRENT");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start.minusHours(2), result.get(0).getStart());
        assertEquals(end.plusHours(2), result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetWaitingBookingsForUserWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(bookingDao.findWaitingOrRejectedByBooker(1, Status.WAITING, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForUser(from, size, 1, "WAITING");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetRejevtedBookingsForUserWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.REJECTED);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));
        Mockito.when(bookingDao.findWaitingOrRejectedByBooker(1, Status.REJECTED, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForUser(from, size, 1, "REJECTED");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.REJECTED, result.get(0).getStatus());
    }

    @Test
    void shouldThrowValidationExceptionStatusNotFoundInGetBookingsForUser() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> service.getAllForUser(null, null, 1, "NOT FOUND"));
    }

    @Test
    void shouldGetAllBookingsForOwnerWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        User owner = new User(2, "pa@op.ru", "owner");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(2)).thenReturn(Optional.of(owner));
        Mockito.when(bookingDao.findAllByOwner(2, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForOwner(from, size, 2, "ALL");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetFutureBookingsForOwnerWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        User owner = new User(2, "pa@op.ru", "owner");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(2)).thenReturn(Optional.of(owner));
        Mockito.when(bookingDao.findFutureByOwner(2, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForOwner(from, size, 2, "FUTURE");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetPastBookingsForOwnerWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        User owner = new User(2, "pa@op.ru", "owner");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start.minusHours(2), end.minusHours(2), item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(2)).thenReturn(Optional.of(owner));
        Mockito.when(bookingDao.findPastByOwner(2, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForOwner(from, size, 2, "PAST");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start.minusHours(2), result.get(0).getStart());
        assertEquals(end.minusHours(2), result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetCurrentBookingsForOwnerWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        User owner = new User(2, "pa@op.ru", "owner");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start.minusHours(2), end.plusHours(2), item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(2)).thenReturn(Optional.of(owner));
        Mockito.when(bookingDao.findCurrentByOwner(2, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForOwner(from, size, 2, "CURRENT");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start.minusHours(2), result.get(0).getStart());
        assertEquals(end.plusHours(2), result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetWaitingBookingsForOwnerWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        User owner = new User(2, "pa@op.ru", "owner");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.WAITING);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(2)).thenReturn(Optional.of(owner));
        Mockito.when(bookingDao.findWaitingOrRejectedByOwner(2, Status.WAITING, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForOwner(from, size, 2, "WAITING");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.WAITING, result.get(0).getStatus());
    }

    @Test
    void shouldGetRejevtedBookingsForOwnerWithPagebale() {
        int from = 0;
        int size = 2;
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User booker = new User(1, "op@pa.ru", "booker");
        User owner = new User(2, "pa@op.ru", "owner");
        Item item = new Item(1, 2, "item", "description", true, null);
        Booking booking = new Booking(1, start, end, item, booker, Status.REJECTED);
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        Mockito.when(userDao.findById(2)).thenReturn(Optional.of(owner));
        Mockito.when(bookingDao.findWaitingOrRejectedByOwner(2, Status.REJECTED, pagebale)).thenReturn(Collections.singletonList(booking));

        List<BookingDtoResp> result = service.getAllForOwner(from, size, 2, "REJECTED");

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(start, result.get(0).getStart());
        assertEquals(end, result.get(0).getEnd());
        assertEquals(item.getId(), result.get(0).getItem().getId());
        assertEquals(booker.getId(), result.get(0).getBooker().getId());
        assertEquals(Status.REJECTED, result.get(0).getStatus());
    }

    @Test
    void shouldThrowValidationExceptionStatusNotFoundInGetBookingsForOwner() {
        BookingService service = new BookingServiceImpl(bookingDao, itemDao, userDao);
        User owner = new User(2, "pa@op.ru", "owner");
        Mockito.when(userDao.findById(1)).thenReturn(Optional.of(owner));

        assertThrows(ValidationException.class, () -> service.getAllForUser(null, null, 1, "NOT FOUND"));
    }
}