package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.dao.BookingDao;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemDao itemDao;
    private BookingDao bookingDao;
    private UserService userService;

    public ItemServiceImpl(ItemDao itemDao, BookingDao bookingDao, UserService userService) {
        this.itemDao = itemDao;
        this.bookingDao = bookingDao;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        ItemValidator.validateItemDto(itemDto);
        checkOwner(userId);
        Item item = ItemMapper.toItemWithoutId(itemDto, userId);
            return ItemMapper.toItemDto(itemDao.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        checkOwner(userId);
        Item itemBefore = ItemMapper.toItem(findItemById(itemId));
        if (itemBefore == null) {
            return null;
        }
        checkOwnerToItem(userId, itemBefore.getOwnerId());
        Item itemAfter = ItemMapper.combineItemWithItemDto(itemBefore, itemDto);
        return ItemMapper.toItemDto(itemDao.save(itemAfter));
    }

    @Override
    public ItemDto findItemById(int itemId) {
        return ItemMapper.toItemDto(itemDao.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена")));
    }

    @Override
    public ItemDtoPers findItemByIdAndUserId(Integer ownerId, int itemId) {
        Item item = itemDao.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));

        Booking last = null;
        Booking next = null;
        if (ownerId != null && item.getOwnerId() == ownerId) {
            List<Booking> lasts = bookingDao.findBookingWithLastNearestDateByItemId(itemId);
            last = CollectionUtils.isEmpty(lasts) ? null : lasts.stream().findFirst().get();
            List<Booking> nexts = bookingDao.findBookingWithNextNearestDateByItemId(itemId);
            next = CollectionUtils.isEmpty(nexts) ? null : nexts.stream().findFirst().get();

        }
        return ItemMapper.toItemDtoPers(item, last, next);
    }

    @Override
    public List<ItemDtoPers> findItemsByUserId(int userId) {
        return itemDao.findItemByOwnerIdOrderById(userId).stream()
                .map(item -> {
                    Booking last = null;
                    Booking next = null;
                    if (item.getOwnerId() == userId) {
                        List<Booking> lasts = bookingDao.findBookingWithLastNearestDateByItemId(item.getId());
                        last = CollectionUtils.isEmpty(lasts) ? null : lasts.stream().findFirst().get();
                        List<Booking> nexts = bookingDao.findBookingWithNextNearestDateByItemId(item.getId());
                        next = CollectionUtils.isEmpty(nexts) ? null : nexts.stream().findFirst().get();
                    }
                    return ItemMapper.toItemDtoPers(item, last, next);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        if (!StringUtils.hasText(text)) {
            return Collections.emptyList();
        }
        return itemDao.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text).stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkOwner(int ownerId) {
        if (!userService.isExist(ownerId)) {
            throw new NotFoundException("Такого владельца нет");
        }
    }

    private void checkOwnerToItem(int outerOwnerId, int innerOwnerId) {
        if (outerOwnerId != innerOwnerId) {
            throw new NotFoundException("Такого владельца нет");
        }
    }

    @Override
    public boolean isExistItem(int itemId) {
        return findItemById(itemId) != null;
    }
}
