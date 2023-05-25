package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(ItemDto itemDto, int userId);

    Item updateItem(ItemDto itemDto, int userId, int itemId);

    Item findItemById(int itemId);

    List<Item> findItemsByUserId(int userId);

    List<Item> searchItemsByText(String text);
}
