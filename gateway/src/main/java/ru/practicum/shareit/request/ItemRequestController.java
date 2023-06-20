package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestReqDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constants.Constatnts.USER_ID;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping()
    public ResponseEntity<Object> addItemRequest(@RequestBody @Valid ItemRequestReqDto itemRequestReqDto,
                                                 @RequestHeader(name = USER_ID) int userId) {
        log.info("Adding request {} by user id={}", itemRequestReqDto, userId);
        return itemRequestClient.addItemRequest(itemRequestReqDto, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(required = false, name = USER_ID) Integer userId) {
        if (userId != null) {
            log.info("Find requests for owner ownerId={}", userId);
            return itemRequestClient.getOwnItemRequests(userId);
        }
        log.info("Find requests for all users");
        return itemRequestClient.getItemRequests();
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(name = USER_ID) int userId,
                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(defaultValue = "100") Integer size) {
        log.info("Find all requests by user userId={}", userId);
        return itemRequestClient.getAllItemRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(name = USER_ID) int userId,
                                             @PathVariable int requestId) {
        log.info("Find request id={} by user id={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}
