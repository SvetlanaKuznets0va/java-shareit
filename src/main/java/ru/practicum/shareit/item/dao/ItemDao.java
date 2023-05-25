package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemDao {
    Item addItem(ItemDto itemDto, int userId);

    Item updateItem(Item item);

    Item findItemById(int itemId);
}
