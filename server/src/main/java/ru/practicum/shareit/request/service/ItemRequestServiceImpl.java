package ru.practicum.shareit.request.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestDao;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final Logger log = LoggerFactory.getLogger(ItemRequestServiceImpl.class);

    UserService userService;
    ItemRequestDao itemRequestDao;
    ItemDao itemDao;

    @Autowired
    public ItemRequestServiceImpl(UserService userService, ItemRequestDao itemRequestDao, ItemDao itemDao) {
        this.userService = userService;
        this.itemRequestDao = itemRequestDao;
        this.itemDao = itemDao;
    }

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, int userId) {
        checkUser(userId);
        ItemRequest ir = ItemRequestMapper.toItemRequest(itemRequestDto, userId);
        ir.setCreated(LocalDateTime.now());
        ItemRequestDto ird = ItemRequestMapper.toItemRequestDto(itemRequestDao.save(ir));
        log.info("Добавлен запрос id={} пользователем id={}", ird.getId(), userId);
        return ird;
    }

    @Override
    public List<ItemRequestRespDto> getOwnItemRequests(int userId) {
        checkUser(userId);
        List<ItemRequest> ownItemRequests = itemRequestDao.findItemRequestsByRequestorIdOrderByCreatedDesc(userId);
        List<Integer> ids = ownItemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemDao.findItemsByRequestIds(ids);
        return ownItemRequests.stream()
                .map(ir -> ItemRequestMapper.toItemRequestRespDto(ir, items))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestRespDto> getItemRequests() {
        List<ItemRequest> itemRequests = itemRequestDao.findAllByOrderByCreatedDesc();
        List<Item> items = itemDao.findAll();
        return itemRequests.stream()
                .map(ir -> ItemRequestMapper.toItemRequestRespDto(ir, items))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestRespDto> getAllItemRequests(Integer from, Integer size, int userId) {
        Pageable pagebale = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").descending());
        List<Item> items = itemDao.findAll();

        return itemRequestDao.findAll(pagebale).stream()
                .filter(ir -> ir.getRequestorId() != userId)
                .map(ir -> ItemRequestMapper.toItemRequestRespDto(ir, items))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestRespDto getItemRequestById(int userId, int requestId) {
        checkUser(userId);
        ItemRequest ir = itemRequestDao.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<Item> items = itemDao.findItemsByRequestIds(Collections.singletonList(requestId));
        return ItemRequestMapper.toItemRequestRespDto(ir, items);
    }

    private void checkUser(int userId) {
        if (!userService.isExist(userId)) {
            log.info("Пользователь с несуществующим id={}", userId);
            throw new NotFoundException("Такого владельца нет");
        }
    }
}
