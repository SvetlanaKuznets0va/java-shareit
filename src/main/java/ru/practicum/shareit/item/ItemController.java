package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    /*private ItemService itemService;

    @Autowired
    public ItemController(UserService itemService) {
        this.itemService = itemService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Item addItem(@Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(item);
    }*/
}
