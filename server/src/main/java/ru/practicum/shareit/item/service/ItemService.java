package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int userId, int itemId);

    ItemDto findItemById(int itemId);

    ItemDtoPers findItemByIdAndUserId(Integer ownerId, int itemId);

    List<ItemDtoPers> findItemsByUserId(Integer from, Integer size, int userId);

    List<ItemDto> searchItemsByText(Integer from, Integer size, String text);

    CommentDto addComment(int userId, int itemId, CommentDto text);
}
