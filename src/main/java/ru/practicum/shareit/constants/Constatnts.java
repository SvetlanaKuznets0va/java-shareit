package ru.practicum.shareit.constants;

import ru.practicum.shareit.exception.ValidationException;

public class Constatnts {
    public static final String USER_ID = "X-Sharer-User-Id";

    public static boolean checkPagination(Integer from, Integer size) {
        if (from == null || size == null) {
            return false;
        }
        if (from < 0 || size <= 0) {
            throw new ValidationException("Поисковые значения заданы не верно");
        }
        return true;
    }
}
