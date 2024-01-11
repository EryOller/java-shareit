package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDtoRq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRs;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoRs createItemRequest(Integer userId, ItemRequestDtoRq itemRequestDto);

    List<ItemRequestDtoRs> getListItemRequest(Integer userId);

    List<ItemRequestDtoRs> getListItemRequestWithPagination(Integer userId, Integer from, Integer size);

    ItemRequestDtoRs getItemRequestById(Integer userId, Integer requestId);

    ItemRequest getItemRequestById(Integer requestId);
}