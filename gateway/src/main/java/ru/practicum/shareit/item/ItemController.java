package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                             @Valid @RequestBody ItemSaveDtoRq itemDto) {
        log.info("Получен запрос POST /items — на создание вещи");
        return itemClient.create(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer itemId,
                           @Valid @RequestBody ItemUpdateDtoRq itemDto) {
        log.info("Получен запрос PATCH /items/{itemId} — на редактирование вещи по itemId");
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer itemId) {
        log.info("Получен запрос GET /items/{itemId} — на получение вещи по itemId");
       return itemClient.get(userId, itemId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllForBooker(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("поступил запрос на получение списка вещей постранично");
        return itemClient.get(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size
    ) {
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDtoRq commentDtoRq,
                                      @PathVariable int itemId,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("пользователь с id {} оставил отзыв на вещь с id {}: {}", userId, itemId, commentDtoRq);
        return itemClient.comment(userId, itemId, commentDtoRq);
    }
}
