package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    Item addItem(ItemDto itemDto, int userId);

    Item updateItem(ItemDto itemDto, int userId, int itemId);

    Item findItemById(int itemId);
}
