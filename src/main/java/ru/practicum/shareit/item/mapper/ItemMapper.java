package ru.practicum.shareit.item.mapper;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getOwnerId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item combineItemWithItemDto(Item item, ItemDto itemDto) {
        return new Item(
                item.getId(),
                item.getOwnerId(),
                StringUtils.hasText(itemDto.getName()) ? itemDto.getName() : item.getName(),
                StringUtils.hasText(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.isAvailable()
        );
    }

    public static Item toItemWithoutId(ItemDto itemDto, int userId) {
        return new Item(
                userId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return itemDto == null ? null : new Item(itemDto.getId(), itemDto.getOwnerId(),
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }
}
