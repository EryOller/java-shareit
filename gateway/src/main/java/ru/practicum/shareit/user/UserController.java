package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping()
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDtoRq userDto) {
        log.info("Получен запрос POST /users — на создание пользователя");
        return userClient.create(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Integer id) {
        log.info("Получен запрос GET /users/{id} — на получение пользователя по id");
        return userClient.get(id);
    }

    @GetMapping()
    public ResponseEntity<Object> getUsers() {
        log.info("Получен запрос GET /users — на получение списка пользователей");
        return userClient.get();
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        log.info("Получен запрос DELETE /users/{id} — на удаление пользователя по id");
        userClient.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserById(@PathVariable Integer id, @Valid @RequestBody UserUpdateDtoRq userDto) {
        log.info("Получен запрос PATCH /users/{id} — на обновление пользователя по id");
        return userClient.update(id, userDto);
    }
}
