package ru.practicum.shareit.booking;

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
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDtoResp add(@RequestBody BookingDto bookingDto, @RequestHeader(name = USER_ID) int userId) {
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResp approve(@RequestHeader(name = USER_ID) int ownerId, @PathVariable int bookingId,
                                  @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResp getByBookingId(@RequestHeader(name = USER_ID) int userId, @PathVariable int bookingId) {
        return bookingService.getByBookingId(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoResp> getAllForUser(@RequestHeader(name = USER_ID) int userId,
                                              @RequestParam() String state,
                                              @RequestParam Integer from,
                                              @RequestParam Integer size) {
        return bookingService.getAllForUser(from, size, userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResp> getAllForOwner(@RequestHeader(name = USER_ID) int userId,
                                               @RequestParam() String state,
                                               @RequestParam Integer from,
                                               @RequestParam Integer size) {
        return bookingService.getAllForOwner(from, size, userId, state);
    }

}
