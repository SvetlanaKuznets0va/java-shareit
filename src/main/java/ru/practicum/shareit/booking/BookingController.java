package ru.practicum.shareit.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResp;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.constants.Constatnts.USER_ID;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final Logger log = LoggerFactory.getLogger(BookingController.class);
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDtoResp add(@RequestBody BookingDto bookingDto, @RequestHeader(name = USER_ID) int userId) {
        log.info("Добавление записи пользователем id={}", userId);
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResp approve(@RequestHeader(name = USER_ID) int ownerId, @PathVariable int bookingId,
                                  @RequestParam boolean approved) {
        log.info("Запрос подтвержения записи id={} владельцем id={}", bookingId, ownerId);
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResp getByBookingId(@RequestHeader(name = USER_ID) int userId, @PathVariable int bookingId) {
        log.info("Запрос записи id={} пользователем id={}", bookingId, userId);
        return bookingService.getByBookingId(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoResp> getAllForUser(@RequestHeader(name = USER_ID) int userId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос всех записей пользователем id={} в статусе {}", userId, state);
        return bookingService.getAllForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResp> getAllForOwner(@RequestHeader(name = USER_ID) int userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос всех записей владельцем id={} в статусе {}", userId, state);
        return bookingService.getAllForOwner(userId, state);
    }

}
