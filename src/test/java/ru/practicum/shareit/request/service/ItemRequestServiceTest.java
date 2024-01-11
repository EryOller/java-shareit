package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDtoRq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRs;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {


    @Autowired
    private final ItemRequestServiceImpl itemRequestService;

    @MockBean
    private final ItemRequestRepository itemRequestRepository;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @Test
    void create() {
        User requester = User.builder()
                .id(2)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(2))
                .thenReturn(Optional.of(requester));

        ItemRequestDtoRq requestDto = ItemRequestDtoRq.builder()
                .description("description")
                .build();

        LocalDateTime now = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1)
                .description("description")
                .requester(requester)
                .created(now)
                .build();

        when(itemRequestRepository.save(any()))
                .thenReturn(request);

        ItemRequestDtoRs itemRequestDtoCreated = itemRequestService.createItemRequest(2, requestDto);
        assertThat(itemRequestDtoCreated, is(notNullValue()));
    }

    @Test
    void throwUserNotFoundException() {
        when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        ItemRequestDtoRq requestDto = ItemRequestDtoRq.builder()
                .description("description")
                .build();

        EntityNotFoundException invalidUserIdException;

        invalidUserIdException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.createItemRequest(2, requestDto));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));

        invalidUserIdException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getListItemRequestWithPagination(2, 0, 10));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));

        invalidUserIdException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequestById(2, 1));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь не найден"));
    }

    @Test
    void getRequestListRelatedToRequester() {
        User requester = User.builder()
                .id(2)
                .name("name2")
                .email("user2@email.com")
                .build();

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(2))
                .thenReturn(new ArrayList<>());

        List<ItemRequestDtoRs> itemRequestDtos = itemRequestService.getListItemRequest(2);

        assertTrue(itemRequestDtos.isEmpty());

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1)
                .description("description")
                .requester(requester)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = List.of(request);

        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(2))
                .thenReturn(itemRequests);

        User owner = User.builder()
                .id(1)
                .name("name1")
                .email("user1@email.com")
                .build();

        List<Item> items = Collections.emptyList();

        when(itemRepository.findAllByRequestIdIn(List.of(1)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getListItemRequest(2);

        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();

        items = List.of(item);

        when(itemRepository.findAllByRequestIdIn(List.of(1)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getListItemRequest(2);

        assertThat(itemRequestDtos, is(notNullValue()));
    }

    @Test
    void getRequestListOfOtherRequesters() {
        User owner = User.builder()
                .id(1)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        LocalDateTime requestCreationDate = LocalDateTime.now();

        User requester = User.builder()
                .id(2)
                .name("name2")
                .email("user2@email.com")
                .build();

        ItemRequest request = ItemRequest.builder()
                .id(1)
                .description("description")
                .requester(requester)
                .created(requestCreationDate)
                .build();

        List<ItemRequest> itemRequests = new ArrayList<>();

        when(itemRequestRepository.findAllByRequesterIdIsNot(any(), any()))
                .thenReturn(itemRequests);
        List<ItemRequestDtoRs> itemRequestDtos = itemRequestService.getListItemRequestWithPagination(1, 0, 10);
        assertTrue(itemRequestDtos.isEmpty());

        itemRequests = List.of(request);
        when(itemRequestRepository.findAllByRequesterIdIsNot(any(), any()))
                .thenReturn(itemRequests);

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequestIdIn(List.of(1)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getListItemRequestWithPagination(1, 0, 10);
        assertTrue(itemRequestDtos.get(0).getItems().isEmpty());

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequestIdIn(List.of(1)))
                .thenReturn(items);

        itemRequestDtos = itemRequestService.getListItemRequestWithPagination(1, 0, 10);
        assertThat(itemRequestDtos, is(notNullValue()));
    }

    @Test
    void throwPaginationException() {
        User owner = User.builder()
                .id(1)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1))
                .thenReturn(Optional.of(owner));
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemRequestService.getListItemRequestWithPagination(1, -1, 10));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemRequestService.getListItemRequestWithPagination(1, 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));
    }

    @Test
    void getRequestByIdByAnyUser() {
        User owner = User.builder()
                .id(1)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        LocalDateTime requestCreationDate = LocalDateTime.now();

        ItemRequest request = ItemRequest.builder()
                .id(1)
                .description("description")
                .requester(owner)
                .created(requestCreationDate)
                .build();

        when(itemRequestRepository.findItemRequestById(request.getId()))
                .thenReturn(Optional.of(request));

        List<Item> items = Collections.emptyList();
        when(itemRepository.findAllByRequestId(1))
                .thenReturn(items);

        ItemRequestDtoRs itemRequestDto = itemRequestService.getItemRequestById(1, 1);
        assertTrue(itemRequestDto.getItems().isEmpty());

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .request(request)
                .build();
        items = List.of(item);

        when(itemRepository.findAllByRequestId(1))
                .thenReturn(items);

        itemRequestDto = itemRequestService.getItemRequestById(1, 1);

        assertThat(itemRequestDto, is(notNullValue()));
    }

    @Test
    void throwItemRequestNotFoundException() {
        User owner = User.builder()
                .id(1)
                .name("name1")
                .email("user1@email.com")
                .build();

        when(userRepository.findById(1))
                .thenReturn(Optional.of(owner));
        when(userRepository.existsById(anyInt()))
                .thenReturn(true);

        EntityNotFoundException invalidItemRequestIdException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> itemRequestService.getItemRequestById(1, 1));
        assertThat(invalidItemRequestIdException.getMessage(), is("Запрос не найден"));
    }
}
