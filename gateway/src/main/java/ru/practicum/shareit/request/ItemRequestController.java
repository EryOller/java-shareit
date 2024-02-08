package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRq;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                    @Valid @RequestBody ItemRequestDtoRq itemRequestDto) {
        return itemRequestClient.create(userId, itemRequestDto);
        //return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getListItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestClient.get(userId);
        //return itemRequestService.getListItemRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getListItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) throws PaginationException {
        return itemRequestClient.get(userId, from, size);
        //return itemRequestService.getListItemRequestWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer requestId) {
        return itemRequestClient.get(userId, requestId);
        //return itemRequestService.getItemRequestById(userId, requestId);
    }
}
