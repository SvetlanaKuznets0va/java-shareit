package ru.practicum.shareit.item.mapper;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoPers;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getOwnerId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId()
        );
    }

    public static ItemDtoPers toItemDtoPers(Item item, Booking last, Booking next, List<CommentDto> comments) {
        return new ItemDtoPers(
                item.getId(),
                item.getOwnerId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                last == null ? null : BookingMapper.bookingDto(last),
                next == null ? null : BookingMapper.bookingDto(next),
                comments,
                item.getRequestId()
        );
    }

    public static Item combineItemWithItemDto(Item item, ItemDto itemDto) {
        return new Item(
                item.getId(),
                item.getOwnerId(),
                StringUtils.hasText(itemDto.getName()) ? itemDto.getName() : item.getName(),
                StringUtils.hasText(itemDto.getDescription()) ? itemDto.getDescription() : item.getDescription(),
                itemDto.getAvailable() != null ? itemDto.getAvailable() : item.isAvailable(),
                item.getRequestId()
        );
    }

    public static Item toItemWithoutId(ItemDto itemDto, int userId) {
        return new Item(
                userId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId()
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return itemDto == null ? null : new Item(itemDto.getId(), itemDto.getOwnerId(),
                itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId());
    }
}
