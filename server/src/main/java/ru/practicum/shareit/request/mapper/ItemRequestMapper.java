package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto dto, int userId) {
        return new ItemRequest(0, dto.getDescription(), userId, dto.getCreated());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest ir) {
        return new ItemRequestDto(ir.getId(), ir.getDescription(), ir.getCreated());
    }

    public static ItemRequestRespDto toItemRequestRespDto(ItemRequest ir, List<Item> items) {
        return new ItemRequestRespDto(ir.getId(),
                ir.getDescription(),
                ir.getCreated(),
                items.stream()
                        .filter(i -> i.getRequestId() != null)
                        .filter(i -> i.getRequestId() == ir.getId())
                        .map(i -> new ItemRequestRespDto.InnerItemDto(i.getId(),
                                i.getName(),
                                i.getDescription(),
                                i.isAvailable(),
                                i.getRequestId()))
                        .collect(Collectors.toList()));
    }
}
