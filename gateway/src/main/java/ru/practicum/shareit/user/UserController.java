package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDtoRq userDto) {
        log.info("Request POST /users — by create user");
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
