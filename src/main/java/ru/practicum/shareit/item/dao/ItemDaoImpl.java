package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Component
public class ItemDaoImpl implements ItemDao {
    Map<Integer, Item> itemStorage = new HashMap<>();
    private static int id = 0;

    @Override
    public Item addItem(ItemDto itemDto, int userId) {
        Item item = new Item(++id, userId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (itemStorage.containsKey(item.getId())) {
            itemStorage.put(item.getId(), item);
            return item;
        }
        return null;
    }

    @Override
    public Item findItemById(int itemId) {
        if (itemStorage.containsKey(itemId)) {
            return itemStorage.get(itemId);
        }
        return null;
    }
}
