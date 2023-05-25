package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public Item addItem(@RequestBody ItemDto itemDto, @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(name = "X-Sharer-User-Id") int userId,
                           @PathVariable int itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }
}
