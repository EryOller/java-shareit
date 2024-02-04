package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDtoRq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRs;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoRs createItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                              @Valid @RequestBody ItemRequestDtoRq itemRequestDto) {
        return itemRequestService.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDtoRs> getListItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getListItemRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoRs> getListItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) throws PaginationException {
        return itemRequestService.getListItemRequestWithPagination(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoRs getItemRequest(@RequestHeader("X-Sharer-User-Id") Integer userId, @PathVariable Integer requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
