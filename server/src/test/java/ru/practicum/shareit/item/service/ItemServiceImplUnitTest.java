package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.CommentDao;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {
    @Mock
    ItemDao itemDao;
    @Mock
    BookingDao bookingDao;
    @Mock
    CommentDao commentDao;
    @Mock
    UserService userService;

    @Test
    void shouldAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item itemReq = new Item(0, 1, "Item", "Item description", true, null);
        ItemDto itemDtoReq = new ItemDto(0, 1, "Item", "Item description", true, null);
        Item itemResp = new Item(1, 1, "Item", "Item description", true, null);
        Mockito.when(userService.isExist(1)).thenReturn(true);
        Mockito.when(itemDao.save(itemReq)).thenReturn(itemResp);

        ItemDto result = itemService.addItem(itemDtoReq, 1);

        Mockito.verify(userService, Mockito.times(1)).isExist(1);
        Mockito.verify(itemDao, Mockito.times(1)).save(any(Item.class));
        assertEquals(1, result.getId());
        assertEquals(1, result.getOwnerId());
        assertEquals("Item", result.getName());
        assertEquals("Item description", result.getDescription());
        assertTrue(result.getAvailable());
        assertTrue(result.getRequestId() == null);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserIdIsNotExistInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, "Item", "Item description", true, null);
        Mockito.when(userService.isExist(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenAvailableIsNullInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, "Item", "Item description", null, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsNullInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, null, "Item description", true, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsEmptyInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, "", "Item description", true, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenNameIsSpaceInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, " ", "Item description", true, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionIsEmptyInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, "Item", "", true, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionIsNullInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, "Item", null, true, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }

    @Test
    void shouldThrowValidationExceptionWhenDescriptionIsSpaceInAddItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(0, 1, "Item", " ", true, null);

        assertThrows(ValidationException.class, () -> itemService.addItem(itemDtoReq, 1));
    }


    @Test
    void shouldUpdateItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item itemAfter = new Item(1, 1, "Item2", "Item description2", true, null);
        ItemDto itemDtoReq = new ItemDto(1, 1, "Item2", "Item description2", true, null);
        Item itemBefore = new Item(1, 1, "Item", "Item description", true, null);
        Mockito.when(userService.isExist(1)).thenReturn(true);
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(itemBefore));
        Mockito.when(itemDao.save(itemAfter)).thenReturn(itemAfter);
        ItemDto result = itemService.updateItem(itemDtoReq, 1, 1);

        Mockito.verify(userService, Mockito.times(1)).isExist(1);
        Mockito.verify(itemDao, Mockito.times(1)).findById(1);
        Mockito.verify(itemDao, Mockito.times(1)).save(any(Item.class));
        assertEquals(1, result.getId());
        assertEquals(1, result.getOwnerId());
        assertEquals("Item2", result.getName());
        assertEquals("Item description2", result.getDescription());
        assertTrue(result.getAvailable());
        assertTrue(result.getRequestId() == null);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserIdIsNotExistInUpdateItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(1, 1, "Item2", "Item description2", true, null);
        Mockito.when(userService.isExist(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDtoReq, 1, 1));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemIsNotExistInUpdateItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(1, 1, "Item2", "Item description2", true, null);
        Mockito.when(userService.isExist(1)).thenReturn(true);
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDtoReq, 1, 1));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemIsNotOwnerInUpdateItem() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        ItemDto itemDtoReq = new ItemDto(1, 1, "Item2", "Item description2", true, null);
        Item itemBefore = new Item(1, 2, "Item", "Item description", true, null);
        Mockito.when(userService.isExist(1)).thenReturn(true);
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(itemBefore));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDtoReq, 1, 1));
    }


    @Test
    void shouldFindItemById() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item item = new Item(1, 2, "Item", "Item description", true, null);
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(item));

        ItemDto result = itemService.findItemById(1);

        Mockito.verify(itemDao, Mockito.times(1)).findById(1);
        assertEquals(1, result.getId());
        assertEquals(2, result.getOwnerId());
        assertEquals("Item", result.getName());
        assertEquals("Item description", result.getDescription());
        assertTrue(result.getAvailable());
        assertTrue(result.getRequestId() == null);
    }


    @Test
    void shouldFindItemByIdAndUserIdForOwner() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item getItem = new Item(1, 1, "Item 1", "Item description 1", true, null);
        User booker = new User(3, "op@pa.ru", "booker");
        List<Booking> lastBookings = new ArrayList<>();
        lastBookings.add(new Booking(1, LocalDateTime.of(2023, 1, 1, 10, 15),
                LocalDateTime.of(2023, 1, 5, 10, 15), getItem, booker, Status.APPROVED));
        lastBookings.add(new Booking(2, LocalDateTime.of(2023, 1, 1, 11, 15),
                LocalDateTime.of(2023, 1, 5, 10, 15), getItem, booker, Status.APPROVED));

        Booking nextBooking = new Booking(3, LocalDateTime.of(2023, 2, 1, 10, 15),
                LocalDateTime.of(2024, 2, 5, 10, 15), getItem, booker, Status.WAITING);
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(getItem));
        Mockito.when(bookingDao.findBookingWithLastNearestDateByItemId(Collections.singletonList(1)))
                .thenReturn(lastBookings);
        Mockito.when(bookingDao.findBookingWithNextNearestDateByItemId(Collections.singletonList(1)))
                .thenReturn(Collections.singletonList(nextBooking));

        ItemDtoPers result = itemService.findItemByIdAndUserId(1, 1);

        assertEquals(1, result.getId());
        assertEquals(1, result.getOwnerId());
        assertEquals("Item 1", result.getName());
        assertEquals("Item description 1", result.getDescription());
        assertTrue(result.getAvailable());
        assertTrue(result.getRequestId() == null);
        assertEquals(1, result.getLastBooking().getId());
        assertEquals(3, result.getNextBooking().getId());
    }

    @Test
    void shouldFindItemByIdAndUserId() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item getItem = new Item(1, 1, "Item 1", "Item description 1", true, null);

        Mockito.when(commentDao.findCommentsByItemsId(Collections.singletonList(any())))
                .thenReturn(Collections.singletonList(new Comment(1, "comment", 1, 1,
                        LocalDateTime.of(2023, 1, 1, 10, 15))));
        Mockito.when(userService.findUserById(anyInt())).thenReturn(new UserDto(1, "op@pa.ru", "user1"));
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.of(getItem));
        ItemDtoPers result = itemService.findItemByIdAndUserId(2, 1);

        assertEquals(1, result.getId());
        assertEquals(1, result.getOwnerId());
        assertEquals("Item 1", result.getName());
        assertEquals("Item description 1", result.getDescription());
        assertTrue(result.getAvailable());
        assertTrue(result.getRequestId() == null);
        assertEquals(null, result.getLastBooking());
        assertEquals(null, result.getNextBooking());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemIsNotExistInFindItemByIdAndUserId() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Mockito.when(itemDao.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItemByIdAndUserId(1, 1));
    }


    @Test
    void shouldFindItemsByUserIdWithoutPagination() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item item = new Item(1, 1, "item1", "item1", true, null);
        User booker = new User(3, "op@pa.ru", "booker");
        UserDto userComment = new UserDto(4, "po@ap.ru", "commentator");
        List<Booking> lastBookings = new ArrayList<>();
        lastBookings.add(new Booking(1, LocalDateTime.of(2023, 1, 1, 10, 15),
                LocalDateTime.of(2023, 1, 5, 10, 15), item, booker, Status.APPROVED));
        lastBookings.add(new Booking(2, LocalDateTime.of(2023, 1, 1, 11, 15),
                LocalDateTime.of(2023, 1, 5, 10, 15), item, booker, Status.APPROVED));
        List<Booking> nextBookings = new ArrayList<>();
        nextBookings.add(new Booking(3, LocalDateTime.of(2023, 2, 1, 10, 15),
                LocalDateTime.of(2024, 2, 5, 10, 15), item, booker, Status.WAITING));
        nextBookings.add(new Booking(4, LocalDateTime.of(2023, 2, 1, 11, 15),
                LocalDateTime.of(2024, 2, 5, 10, 15), item, booker, Status.WAITING));
        Comment comment = new Comment(1, "comment", 1, 4,
                LocalDateTime.of(2023, 1, 5, 10, 15));

        Mockito.when(itemDao.findAll()).thenReturn(Collections.singletonList(item));
        Mockito.when(bookingDao.findBookingWithLastNearestDateByItemId(Collections.singletonList(1)))
                .thenReturn(lastBookings);
        Mockito.when(bookingDao.findBookingWithNextNearestDateByItemId(Collections.singletonList(1)))
                .thenReturn(nextBookings);
        Mockito.when(commentDao.findCommentsByItemsId(Collections.singletonList(1)))
                .thenReturn(Collections.singletonList(comment));
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(userComment));

        List<ItemDtoPers> result = itemService.findItemsByUserId(null, null, 1);

        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getComments().size() == 1);
        assertEquals(1, result.get(0).getId());
        assertEquals(1, result.get(0).getOwnerId());
        assertEquals("item1", result.get(0).getName());
        assertEquals("item1", result.get(0).getDescription());
        assertTrue(result.get(0).getAvailable());
        assertTrue(result.get(0).getRequestId() == null);
        assertEquals(1, result.get(0).getLastBooking().getId());
        assertEquals(3, result.get(0).getNextBooking().getId());
        assertEquals(1, result.get(0).getComments().get(0).getId());
    }

    @Test
    void shouldFindItemsByUserIdWithPagination() {
        int from = 0;
        int size = 2;
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item item = new Item(1, 1, "item1", "item1", true, null);
        User booker = new User(3, "op@pa.ru", "booker");
        UserDto userComment = new UserDto(4, "po@ap.ru", "commentator");
        Booking lastBooking = new Booking(1, LocalDateTime.of(2023, 1, 1, 10, 15),
                LocalDateTime.of(2023, 1, 5, 10, 15), item, booker, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.of(2023, 2, 1, 10, 15),
                LocalDateTime.of(2024, 2, 5, 10, 15), item, booker, Status.WAITING);
        Comment comment = new Comment(1, "comment", 1, 4,
                LocalDateTime.of(2023, 1, 5, 10, 15));

        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Item> pages = new PageImpl<>(Collections.singletonList(item), pagebale, Collections.singletonList(item).size());

        Mockito.when(itemDao.findAll(pagebale)).thenReturn(pages);
        Mockito.when(bookingDao.findBookingWithLastNearestDateByItemId(Collections.singletonList(1)))
                .thenReturn(Collections.singletonList(lastBooking));
        Mockito.when(bookingDao.findBookingWithNextNearestDateByItemId(Collections.singletonList(1)))
                .thenReturn(Collections.singletonList(nextBooking));
        Mockito.when(commentDao.findCommentsByItemsId(Collections.singletonList(1)))
                .thenReturn(Collections.singletonList(comment));
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(userComment));

        List<ItemDtoPers> result = itemService.findItemsByUserId(0, 2, 1);

        assertTrue(result.size() == 1);
        assertTrue(result.get(0).getComments().size() == 1);
        assertEquals(1, result.get(0).getId());
        assertEquals(1, result.get(0).getOwnerId());
        assertEquals("item1", result.get(0).getName());
        assertEquals("item1", result.get(0).getDescription());
        assertTrue(result.get(0).getAvailable());
        assertTrue(result.get(0).getRequestId() == null);
        assertEquals(1, result.get(0).getLastBooking().getId());
        assertEquals(2, result.get(0).getNextBooking().getId());
        assertEquals(1, result.get(0).getComments().get(0).getId());
    }

    @Test
    void shouldSearchItemsByTextWithoutPagination() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item item = new Item(1, 1, "item", "description", true, null);
        Mockito.when(itemDao.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Item", "Item"))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.searchItemsByText(null, null, "Item");

        assertTrue(result.size() == 1);
        assertEquals(1, result.get(0).getId());
        assertEquals(1, result.get(0).getOwnerId());
        assertEquals("item", result.get(0).getName());
        assertEquals("description", result.get(0).getDescription());
        assertTrue(result.get(0).getAvailable());
        assertTrue(result.get(0).getRequestId() == null);
    }

    @Test
    void shouldSearchItemsByTextWithPagination() {
        int from = 0;
        int size = 2;
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item item = new Item(1, 1, "item", "description", true, null);

        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Item> pages = new PageImpl<>(Collections.singletonList(item), pagebale, Collections.singletonList(item).size());

        Mockito.when(itemDao.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase("Item", "Item", pagebale))
                .thenReturn(pages);
        List<ItemDto> result = itemService.searchItemsByText(from, size, "Item");

        assertTrue(result.size() == 1);
        assertEquals(1, result.get(0).getId());
        assertEquals(1, result.get(0).getOwnerId());
        assertEquals("item", result.get(0).getName());
        assertEquals("description", result.get(0).getDescription());
        assertTrue(result.get(0).getAvailable());
        assertTrue(result.get(0).getRequestId() == null);
    }

    @Test
    void shouldReturnEmptyIfTextEmptyInSearchItemsByText() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);

        List<ItemDto> result = itemService.searchItemsByText(null, null, "");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyIfTextNullInSearchItemsByText() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);

        List<ItemDto> result = itemService.searchItemsByText(null, null, null);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyIfTextSpaceInSearchItemsByText() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);

        List<ItemDto> result = itemService.searchItemsByText(null, null, " ");
        assertTrue(result.isEmpty());
    }


    @Test
    void shouldAddComment() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Item item1 = new Item(1, 1, "item1", "item1", true, null);
        Item item2 = new Item(2, 1, "item2", "item2", true, null);
        User booker = new User(3, "op@pa.ru", "booker");
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1, LocalDateTime.of(2023, 1, 1, 10, 15),
                LocalDateTime.of(2023, 1, 5, 10, 15), item1, booker, Status.APPROVED));
        bookings.add(new Booking(2, LocalDateTime.of(2023, 2, 1, 10, 15),
                LocalDateTime.of(2024, 2, 5, 10, 15), item2, booker, Status.WAITING));
        Comment commentResp = new Comment(1, "comment", 1, 3, LocalDateTime.of(2023, 3, 1, 10, 15));

        Mockito.when(bookingDao.findAllByBooker(3)).thenReturn(bookings);
        Mockito.when(commentDao.save(any())).thenReturn(commentResp);

        CommentDto result = itemService.addComment(3, 1, new CommentDto(0, "comment", null, null));

        assertEquals(1, result.getId());
    }

    @Test
    void shouldThrowValidationExceptionIfTextIsEmptyInAddComment() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);

        assertThrows(ValidationException.class, () -> itemService.addComment(3, 1, new CommentDto(0, "", null, null)));
    }

    @Test
    void shouldThrowValidationExceptionIfBookingIsEmptyInAddComment() {
        ItemService itemService = new ItemServiceImpl(itemDao, bookingDao, commentDao, userService);
        Mockito.when(bookingDao.findAllByBooker(2)).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(2, 1, new CommentDto(0, "comment", null, null)));
    }
}