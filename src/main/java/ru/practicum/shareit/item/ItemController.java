package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDtoRs createItem(@RequestHeader("X-Sharer-User-Id") int ownerId,
                                @Valid @RequestBody ItemSaveDtoRq itemDto) {
        log.info("Получен запрос POST /items — на создание вещи");
        return itemService.save(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoRs updateItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer itemId,
                           @Valid @RequestBody ItemUpdateDtoRq itemDto) {
        log.info("Получен запрос PATCH /items/{itemId} — на редактирование вещи по itemId");
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoRs getItem(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer itemId) {
        log.info("Получен запрос GET /items/{itemId} — на получение вещи по itemId");
       return itemService.getItemById(userId, itemId);
    }

    @GetMapping()
    public List<ItemDtoRs> getAllForBooker(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("поступил запрос на получение списка вещей постранично");
        return itemService.getAllItemWithPagination(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoRs> getAllItemsByText(@RequestParam String text,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("Получен запрос GET /items/search — на поиск вещей по тексту");
        return itemService.searchItemByTextWithPagination(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoRs createComment(@Valid @RequestBody CommentDtoRq commentDtoRq,
                                      @PathVariable int itemId,
                                      @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("пользователь с id {} оставил отзыв на вещь с id {}: {}", userId, itemId, commentDtoRq);

        return itemService.createComment(commentDtoRq, itemId, userId);
    }
}
