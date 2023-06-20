package ru.practicum.shareit.item.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private ItemDao itemDao;
    private BookingDao bookingDao;
    private CommentDao commentDao;
    private UserService userService;

    public ItemServiceImpl(ItemDao itemDao, BookingDao bookingDao, CommentDao commentDao, UserService userService) {
        this.itemDao = itemDao;
        this.bookingDao = bookingDao;
        this.commentDao = commentDao;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        checkOwner(userId);
        Item item = ItemMapper.toItemWithoutId(itemDto, userId);
        ItemDto result = ItemMapper.toItemDto(itemDao.save(item));
        log.info("Добавлена вещь: {}, пользователем id={}", result.getName(), userId);
        return result;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        checkOwner(userId);
        Item itemBefore = ItemMapper.toItem(findItemById(itemId));
        checkOwnerToItem(userId, itemBefore.getOwnerId());
        Item itemAfter = ItemMapper.combineItemWithItemDto(itemBefore, itemDto);
        ItemDto result = ItemMapper.toItemDto(itemDao.save(itemAfter));
        log.info("Обновлена вещь {}, пользователем id={}", result.getId(), userId);
        return result;
    }

    @Override
    public ItemDto findItemById(int itemId) {
        return ItemMapper.toItemDto(getItem(itemId));
    }

    @Override
    public ItemDtoPers findItemByIdAndUserId(Integer ownerId, int itemId) {
        Item item = getItem(itemId);

        Booking last = null;
        Booking next = null;
        if (ownerId != null && item.getOwnerId() == ownerId) {
            List<Booking> lasts = bookingDao.findBookingWithLastNearestDateByItemId(Collections.singletonList(itemId));
            last = CollectionUtils.isEmpty(lasts) ? null : lasts.stream().findFirst().get();
            List<Booking> nexts = bookingDao.findBookingWithNextNearestDateByItemId(Collections.singletonList(itemId));
            next = CollectionUtils.isEmpty(nexts) ? null :
                    nexts.stream()
                            .filter(i -> i.getStatus() != Status.REJECTED)
                            .findFirst().orElse(null);

        }
        return ItemMapper.toItemDtoPers(item, last, next, getListCommentsDto(itemId));
    }

    @Override
    public List<ItemDtoPers> findItemsByUserId(Integer from, Integer size, int userId) {
        List<Item> items;

        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);

        items = itemDao.findAll(pagebale).stream().filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());

        List<Integer> groupItemId = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Integer, Booking> lasts = bookingDao.findBookingWithLastNearestDateByItemId(groupItemId).stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b,
                        (o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? o1 : o2));
        Map<Integer, Booking> nexts = bookingDao.findBookingWithNextNearestDateByItemId(groupItemId).stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(), b -> b,
                        (o1, o2) -> o1.getStart().isBefore(o2.getStart()) ? o1 : o2));

        List<Comment> comments = commentDao.findCommentsByItemsId(groupItemId);
        Function<Comment, CommentDto> commentToCommentDto = comment -> CommentMapper.toCommentDto(comment,
                getUserNamesById().get(comment.getAuthorName()));
        return items.stream()
                .map(item -> ItemMapper
                        .toItemDtoPers(item, lasts.get(item.getId()), nexts.get(item.getId()), comments.stream()
                                .map(commentToCommentDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(Integer from, Integer size, String text) {
            Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size);
            return itemDao.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text, pagebale).stream()
                    .filter(Item::isAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int userId, int itemId, CommentDto text) {
        List<Booking> userBookings = bookingDao.findAllByBooker(userId);
        Optional<Booking> booking = userBookings.stream()
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .filter(b -> b.getStatus() != Status.REJECTED)
                .filter(b -> b.getItem().getId() == itemId)
                .findFirst();

        if (booking.isPresent()) {
            Comment comment = CommentMapper.toComment(booking.get(), text);
            return CommentMapper.toCommentDto(commentDao.save(comment), booking.get().getBooker().getName());
        }
        throw new ValidationException("Вещь не была в аренде");
    }

    private void checkOwner(int ownerId) {
        if (!userService.isExist(ownerId)) {
            log.info("Владелец с несуществующим id={}", ownerId);
            throw new NotFoundException("Такого владельца нет");
        }
    }

    private void checkOwnerToItem(int outerOwnerId, int innerOwnerId) {
        if (outerOwnerId != innerOwnerId) {
            log.info("Не совпадают id владельца. Запрос от id={} Владелец id={}", outerOwnerId, innerOwnerId);
            throw new NotFoundException("Такого владельца нет");
        }
    }

    private Item getItem(int itemId) {
        return itemDao.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
    }

    private List<CommentDto> getListCommentsDto(int itemId) {
        return commentDao.findCommentsByItemsId(Collections.singletonList(itemId)).stream()
                .map(comment -> {
                    String name = userService.findUserById(comment.getAuthorName()).getName();
                    return CommentMapper.toCommentDto(comment, name);
                })
                .collect(Collectors.toList());
    }

    private Map<Integer, String> getUserNamesById() {
        List<UserDto> users = userService.getAllUsers();
        return users.stream()
                .collect(Collectors.toMap(UserDto::getId, UserDto::getName));
    }
}
