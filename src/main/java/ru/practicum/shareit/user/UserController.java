package ru.practicum.shareit.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Добавление пользователя");
        return userService.addUser(userDto);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable int id) {
        log.info("Обновление пользователя id={}", id);
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        log.info("Удаление пользователя id={}", id);
        userService.deleteUserById(id);
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable int id) {
        log.info("Поиск пользователя id={}", id);
        return userService.findUserById(id);
    }
}
