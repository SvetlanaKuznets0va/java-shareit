package ru.practicum.shareit.item.validator;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemValidator {
    public static void validateItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("У вещи нет информации о доступности");
        }
        if (!StringUtils.hasText(itemDto.getName())) {
            throw new ValidationException("У вещи нет названия");
        }
        if (!StringUtils.hasText(itemDto.getDescription())) {
            throw new ValidationException("У вещи нет описания");
        }
    }
}
