package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int userId, int itemId);

    ItemDto findItemById(int itemId);

    ItemDtoPers findItemByIdAndUserId(Integer ownerId, int itemId);

    List<ItemDtoPers> findItemsByUserId(int userId);

    List<ItemDto> searchItemsByText(String text);

    boolean isExistItem(int itemId);
}
