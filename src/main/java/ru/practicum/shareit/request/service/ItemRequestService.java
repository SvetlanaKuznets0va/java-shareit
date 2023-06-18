package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequestDto itemRequestReqDto, int userId);

    List<ItemRequestRespDto> getOwnItemRequests(int userId);

    List<ItemRequestRespDto> getItemRequests();

    List<ItemRequestRespDto> getAllItemRequests(Integer from, Integer size, int userId);

    ItemRequestRespDto getItemRequestById(int userId, int requestId);
}
