package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserUpdRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        log.info("Adding user {}", userRequestDto);
        return userClient.addUser(userRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserUpdRequestDto userUpdRequestDto,
                                             @Positive @PathVariable int id) {
        log.info("Updating user with id={} to user {}", id, userUpdRequestDto);
        return userClient.updateUser(id, userUpdRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@Positive @PathVariable int id) {
        log.info("Deleting user with id={}", id);
        return userClient.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@Positive @PathVariable int id) {
        log.info("Find user with id={}", id);
        return userClient.findUserById(id);
    }
}
