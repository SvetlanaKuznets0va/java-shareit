package ru.practicum.shareit.request.validator;

import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestValidator {
    public static void validateItemRequestDto(ItemRequestDto itemRequestDto) {
        if (!StringUtils.hasText(itemRequestDto.getDescription())) {
            throw new ValidationException("Запрос вещи без описания");
        }
    }
}
