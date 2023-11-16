package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public UserDtoRs createUser(@Valid @RequestBody UserCreateDtoRq userDto) {
        log.info("Получен запрос POST /users — на создание пользователя");
        return userService.save(userDto);
    }

    @GetMapping("/{id}")
    public UserDtoRs getUserById(@PathVariable Integer id) {
        log.info("Получен запрос GET /users/{id} — на получение пользователя по id");
        return userService.findById(id);
    }

    @GetMapping()
    public List<UserDtoRs> getUsers() {
        log.info("Получен запрос GET /users — на получение списка пользователей");
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        log.info("Получен запрос DELETE /users/{id} — на удаление пользователя по id");
        userService.deleteUserById(id);
    }

    @PatchMapping("/{id}")
    public UserDtoRs updateUserById(@PathVariable Integer id, @Valid @RequestBody UserUpdateDtoRq userDto) {
        log.info("Получен запрос PATCH /users/{id} — на обновление пользователя по id");
        return userService.updateUserById(id, userDto);
    }
}
