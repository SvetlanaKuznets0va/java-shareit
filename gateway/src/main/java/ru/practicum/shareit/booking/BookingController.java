package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static ru.practicum.shareit.constants.Constatnts.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(name = USER_ID) int userId,
                                      @RequestBody @Valid BookItemRequestDto requestDto) {
        LocalDateTime start = requestDto.getStart();
        LocalDateTime end = requestDto.getEnd();
        if (end.isBefore(start) || end.isBefore(LocalDateTime.now()) || start.isBefore(LocalDateTime.now())
                || start.isEqual(end)) {
            return ResponseEntity.badRequest().build();
        }
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.add(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(name = USER_ID) int ownerId,
                                          @PathVariable int bookingId,
                                          @RequestParam Boolean approved) {
        return bookingClient.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getByBookingId(@RequestHeader(name = USER_ID) int userId,
                                                 @PathVariable int bookingId) {
        return bookingClient.getByBookingId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllForUser(@RequestHeader(name = USER_ID) int userId,
                                                @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "100") Integer size) {

        Optional<BookingState> state = BookingState.from(stateParam);

        if(state.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"));
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllForUser(userId, state.get(), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForOwner(@RequestHeader(name = USER_ID) int userId,
                                                 @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "100") Integer size) {
        Optional<BookingState> state = BookingState.from(stateParam);

        if(state.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unknown state: UNSUPPORTED_STATUS"));
        }
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllForOwner(userId, state.get(), from, size);
    }
}
