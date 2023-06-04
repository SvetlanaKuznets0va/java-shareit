package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
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
    private UserService userService;

    public ItemServiceImpl(ItemDao itemDao, UserService userService) {
        this.itemDao = itemDao;
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
    public List<ItemDto> findItemsByUserId(int userId) {
        return itemDao.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
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
}
