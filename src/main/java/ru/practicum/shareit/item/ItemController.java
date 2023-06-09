package ru.practicum.shareit.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constants.Constatnts.USER_ID;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final Logger log = LoggerFactory.getLogger(ItemController.class);

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto addItem(@RequestBody ItemDto itemDto, @RequestHeader(name = USER_ID) int userId) {
        log.info("Добавление вещи пользователем id=" + userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = USER_ID) int userId,
                              @PathVariable int itemId) {
        log.info("Обновление вещи id=" + itemId + " пользователем id=" + userId);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoPers getItemById(@RequestHeader(required = false, name = USER_ID) Integer userId, @PathVariable int itemId) {
        log.info("Запрос вещи id=" + itemId + " пользователем id=" + userId);
        return itemService.findItemByIdAndUserId(userId, itemId);
    }

    @GetMapping()
    public List<ItemDtoPers> getItemByUserId(@RequestHeader(name = USER_ID) int userId) {
        log.info("Запрос вещей пользователем id=" + userId);
        return itemService.findItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        log.info("Запрос вещей сожержащих текст: " + text);
        return itemService.searchItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(name = USER_ID) int userId, @PathVariable int itemId,
                                 @RequestBody CommentDto text) {
        log.info("Добавление комментария вещи id=" + itemId + " пользователем id=" + userId);
        return itemService.addComment(userId, itemId, text);
    }
}
