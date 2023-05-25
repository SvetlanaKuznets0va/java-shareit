package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.service.UserService;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemDao itemDao;
    private UserService userService;

    public ItemServiceImpl(ItemDao itemDao, UserService userService) {
        this.itemDao = itemDao;
        this.userService = userService;
    }

    @Override
    public Item addItem(ItemDto itemDto, int userId) {
        checkOwner(userId);
        ItemValidator.validateItemDto(itemDto);
        return itemDao.addItem(itemDto, userId);
    }

    @Override
    public Item updateItem(ItemDto itemDto, int userId, int itemId) {
        checkOwner(userId);
        Item itemBefore = findItemById(itemId);
        if (itemBefore == null) {
            return null;
        }
        checkOwnerToItem(userId, itemBefore.getOwnerId());
        Item itemAfter = ItemMapper.CombineItemWithItemDto(itemBefore, itemDto);
        return itemDao.updateItem(itemAfter);
    }

    @Override
    public Item findItemById(int itemId) {
        return itemDao.findItemById(itemId);
    }

    private void checkOwner(int ownerId) {
        if (userService.findUserById(ownerId) == null) {
            throw new NotFoundException("Такого владельца нет");
        }
    }

    private void checkOwnerToItem(int outerOwnerId, int innerOwnerId) {
        if (outerOwnerId != innerOwnerId) {
            throw new NotFoundException("Такого владельца нет");
        }
    }
}
