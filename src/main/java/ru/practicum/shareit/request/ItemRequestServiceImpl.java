package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.pagination_manager.PaginationManager;
import ru.practicum.shareit.request.dto.ItemRequestDtoRq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRs;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDtoRs createItemRequest(Integer userId, ItemRequestDtoRq itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDtoRs(itemRequest);
    }

    @Override
    public List<ItemRequestDtoRs> getListItemRequest(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }
        List<ItemRequestDtoRs> itemRequestDtos = itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDtoRs)
                .collect(Collectors.toList());

        List<Integer> requestIdList = itemRequestDtos.stream()
                .map(ItemRequestDtoRs::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);

        for (ItemRequestDtoRs itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .collect(Collectors.toList());
            if (!requestItems.isEmpty()) {
                List<ItemDtoRs> itemDtos = requestItems.stream()
                        .map(itemMapper::toItemDtoRs)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDtoRs> getListItemRequestWithPagination(Integer userId, Integer from, Integer size)
            throws PaginationException {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        PageRequest pageRequest = PaginationManager.form(from.intValue(), size.intValue(), Sort.Direction.DESC,
                "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdIsNot(userId, pageRequest);

        List<ItemRequestDtoRs> itemRequestDtos = itemRequests.stream()
                .map(itemRequestMapper::toItemRequestDtoRs)
                .collect(Collectors.toList());

        List<Integer> requestIdList = itemRequestDtos.stream()
                .map(ItemRequestDtoRs::getId)
                .collect(Collectors.toList());
        List<Item> items = itemRepository.findAllByRequestIdIn(requestIdList);

        for (ItemRequestDtoRs itemRequestDto : itemRequestDtos) {
            List<Item> requestItems = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId()))
                    .collect(Collectors.toList());
            if (!requestItems.isEmpty()) {
                List<ItemDtoRs> itemDtos = requestItems.stream()
                        .map(itemMapper::toItemDtoRs)
                        .collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDtoRs getItemRequestById(Integer userId, Integer requestId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        ItemRequestDtoRs itemRequestDto = itemRequestMapper.toItemRequestDtoRs(itemRequest);

        List<Item> items = itemRepository.findAllByRequestId(itemRequestDto.getId());
        if (!items.isEmpty()) {
            List<ItemDtoRs> itemDtos = items.stream()
                    .map(itemMapper::toItemDtoRs)
                    .collect(Collectors.toList());
            itemRequestDto.setItems(itemDtos);
        }
        return itemRequestDto;
    }

    @Override
    public ItemRequest getItemRequestById(Integer requestId) {
        return itemRequestRepository.findItemRequestById(requestId).orElse(null);
    }
}