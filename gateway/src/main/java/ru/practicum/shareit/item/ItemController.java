package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import java.util.Collections;

import static ru.practicum.shareit.booking.constants.Constatnts.USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemRequestDto itemDto,
                                          @RequestHeader(name = USER_ID) int userId) {
        log.info("Adding item {} by user id={}", itemDto, userId);
        return itemClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemRequestDto itemDto,
                                             @RequestHeader(name = USER_ID) int userId,
                                             @PathVariable int itemId) {
        log.info("Updating item id={} ny user id={} to item {}", itemId, userId, itemDto);
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(required = false, name = USER_ID) Integer userId,
                                              @PathVariable int itemId) {
        log.info("Find item with id={} by user with id={}", itemId, userId);
        return itemClient.findItemByIdAndUserId(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemByUserId(@RequestHeader(name = USER_ID) int userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "100") Integer size) {
        log.info("Find items by user with id={}", userId);
        return itemClient.findItemsByUserId(from, size, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@Size(max = 50) @RequestParam String text,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "100") Integer size) {
        log.info("Find items by text: {}", text);
        if(!StringUtils.hasText(text)) {
             return ResponseEntity.ok()
                     .body(Collections.emptyList());
        }
        return itemClient.searchItemsByText(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = USER_ID) int userId,
                                             @PathVariable int itemId,
                                             @RequestBody @Valid CommentRequestDto text) {
        log.info("Adding comment to item with id={} of user with id={}", itemId, userId);
        return itemClient.addComment(userId, itemId, text);
    }
}
