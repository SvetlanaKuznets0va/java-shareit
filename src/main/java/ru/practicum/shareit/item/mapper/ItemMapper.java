package ru.practicum.shareit.item.mapper;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item CombineItemWithItemDto(Item item, ItemDto itemDto) {
        return new Item(
                item.getId(),
                item.getOwnerId(),
                StringUtils.hasText(itemDto.getName()) ? itemDto.getName() : item.getName(),
                StringUtils.hasText(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.isAvailable()
        );
    }
}
