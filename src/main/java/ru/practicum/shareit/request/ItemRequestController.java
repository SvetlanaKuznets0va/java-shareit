package ru.practicum.shareit.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constants.Constatnts.USER_ID;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final Logger log = LoggerFactory.getLogger(ItemRequestController.class);

    private ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping()
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(name = USER_ID) int userId) {
        log.info("Добавление запроса пользователем id={}", userId);
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping()
    public List<ItemRequestRespDto> getOwnItemRequests(@RequestHeader(required = false, name = USER_ID) Integer userId) {
        if (userId != null) {
            log.info("Получение своих запросов пользователем id={}", userId);
            return itemRequestService.getOwnItemRequests(userId);
        }
        log.info("Получение всех запросов");
        return itemRequestService.getItemRequests();
    }

    @GetMapping("/all")
    public List<ItemRequestRespDto> getAllItemRequests(@RequestHeader(name = USER_ID) int userId,
                                                       @RequestParam(required = false) Integer from,
                                                       @RequestParam(required = false) Integer size) {
        log.info("Получение всех запросов пользователем id={}", userId);
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestRespDto getItemRequest(@RequestHeader(name = USER_ID) int userId,
                                             @PathVariable int requestId) {
        log.info("Получение запроса id={} пользователем id={}", requestId, userId);
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
