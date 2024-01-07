package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.EditForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    @Autowired
    private final ItemServiceImpl itemService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final UserService userService;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final BookingService bookingService;

    @MockBean
    private final CommentRepository commentRepository;

    @Test
    void create() {
        User owner = User.builder()
                .id(1)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        ItemSaveDtoRq itemDtoRq = ItemSaveDtoRq.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        Item item = Item.builder()
                .id(1)
                .name(itemDtoRq.getName())
                .description(itemDtoRq.getDescription())
                .available(itemDtoRq.getAvailable())
                .owner(owner)
                .build();

        when(itemRepository.save(any()))
                .thenReturn(item);

        ItemDtoRs itemDtoRs = itemService.save(1, itemDtoRq);

        assertThat(itemDtoRs.getId(), equalTo(item.getId()));
        assertThat(itemDtoRs.getName(), equalTo(item.getName()));
        assertThat(itemDtoRs.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoRs.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void throwUserNotFoundException() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        ItemSaveDtoRq itemSaveDto = ItemSaveDtoRq.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        ItemUpdateDtoRq itemUpdateDto = ItemUpdateDtoRq.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        NotFoundException invalidUserIdException;

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.save(1, itemSaveDto));
        assertThat(invalidUserIdException.getMessage(), is("Владелец вещи с id 1 не найден"));

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1, 1, itemUpdateDto));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь с id 1 не найден"));

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1, 1));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь с id 1 не найден"));

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getAllItemWithPagination(1, 0, 10));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь с id 1 не найден"));

        CommentDtoRq commentDtoRq = CommentDtoRq.builder()
                .text("comment")
                .build();

        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));

        when(userRepository.findById(3))
                .thenReturn(Optional.empty());

        invalidUserIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDtoRq, 3, 1));
        assertThat(invalidUserIdException.getMessage(), is("Пользователь с идентификатором 1 не найден"));
    }

    @Test
    void throwItemNotFoundException() {
        User user = User.builder()
                .id(1)
                .name("name")
                .email("user@email.com")
                .build();

        User owner = User.builder()
                .id(2)
                .name("name")
                .email("user2@email.com")
                .build();

        ItemUpdateDtoRq itemDtoRq = ItemUpdateDtoRq.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        NotFoundException invalidItemIdException;

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(itemRepository.findById(1))
                .thenReturn(Optional.empty());

        invalidItemIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1, 1, itemDtoRq));
        assertThat(invalidItemIdException.getMessage(), is("Пользователь с id 1 не найден"));

        invalidItemIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItemById(1, 1));
        assertThat(invalidItemIdException.getMessage(), is("Пользователь с id 1 не найден"));

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));

        invalidItemIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.update(1, 1, itemDtoRq));
        assertThat(invalidItemIdException.getMessage(), is("Пользователь с id 1 не найден"));

        CommentDtoRq commentDto = CommentDtoRq.builder()
                .text("comment")
                .build();
        when(itemRepository.findById(1))
                .thenReturn(Optional.empty());

        invalidItemIdException = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDto, 1, 3));
        assertThat(invalidItemIdException.getMessage(), is("Пользователь с идентификатором 3 не найден"));
    }

    @Test
    void itemUpdate() {
        User owner = User.builder()
                .id(1)
                .name("name")
                .email("user@email.com")
                .build();
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));

        ItemUpdateDtoRq itemDtoRq = ItemUpdateDtoRq.builder()
                .name("nameUpdated")
                .description("descriptionUpdated")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Item itemUpdated = Item.builder()
                .id(1)
                .name(itemDtoRq.getName())
                .description(itemDtoRq.getDescription())
                .available(true)
                .owner(owner)
                .build();
        when(itemRepository.save(any()))
                .thenReturn(itemUpdated);
        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        ItemDtoRs itemDtoRs = itemService.update(1, 1, itemDtoRq);

        assertThat(itemDtoRs.getId(), equalTo(item.getId()));
        assertThat(itemDtoRs.getName(), equalTo(item.getName()));
        assertThat(itemDtoRs.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoRs.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void getItem() {
        User user = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();

        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(booker)
                .created(created)
                .build();
        List<Comment> commentList = List.of(comment);

        Booking lastBooking = Booking.builder()
                .id(1)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBooking = Booking.builder()
                .id(2)
                .start(created.plusDays(1))
                .end(created.plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        when(commentRepository.findAllCommentsByItemOwnerId(anyInt()))
                .thenReturn(commentList);
        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        ItemDtoRs itemDto = itemService.getItemById(1, 1);
        assertThat(itemDto, is(notNullValue()));

        when(bookingRepository.findPastOwnerBookings(anyInt(), anyInt(),any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findFutureOwnerBookings(anyInt(), anyInt(), any()))
                .thenReturn(Collections.emptyList());
        itemDto = itemService.getItemById(2, 1);
        assertThat(itemDto.getLastBooking(), is(nullValue()));
        assertThat(itemDto.getNextBooking(), is(nullValue()));

        when(bookingRepository.findPastOwnerBookings(anyInt(), anyInt(),any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findFutureOwnerBookings(anyInt(), anyInt(), any()))
                .thenReturn(List.of(nextBooking));
        itemDto = itemService.getItemById(2, 1);
        assertThat(itemDto.getLastBooking().getBookerId(), equalTo(lastBooking.getBooker().getId()));
        assertThat(itemDto.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemDto.getLastBooking().getStart(), equalTo(lastBooking.getStart()));
        assertThat(itemDto.getLastBooking().getEnd(), equalTo(lastBooking.getEnd()));

        assertThat(itemDto.getNextBooking().getBookerId(), equalTo(nextBooking.getBooker().getId()));
        assertThat(itemDto.getNextBooking().getId(), equalTo(nextBooking.getId()));
        assertThat(itemDto.getNextBooking().getStart(), equalTo(nextBooking.getStart()));
        assertThat(itemDto.getNextBooking().getEnd(), equalTo(nextBooking.getEnd()));
    }

    @Test
    void getAllItems() {
        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        when(itemRepository.findAllByOwnerId(any(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemDtoRs> itemRsList = itemService.getAllItemWithPagination(2, 0, 10);
        Assertions.assertTrue(itemRsList.isEmpty());

        Item itemOne = Item.builder()
                .id(1)
                .name("Max")
                .description("Пила")
                .available(true)
                .owner(owner)
                .build();
        Item itemTwo = Item.builder()
                .id(2)
                .name("Oleg")
                .description("Дерево")
                .available(true)
                .owner(owner)
                .build();

        List<Item> items = new ArrayList<>();
        items.add(itemOne);
        items.add(itemTwo);

        when(itemRepository.getListItemsByOwnerIdOrderByIdAsc(anyInt(), any()))
                .thenReturn(items);

        LocalDateTime created = LocalDateTime.now();
        Comment commentOne = Comment.builder()
                .id(1)
                .text("text")
                .item(itemOne)
                .author(booker)
                .created(created)
                .build();

        Comment commentTwo = Comment.builder()
                .id(1)
                .text("text")
                .item(itemTwo)
                .author(booker)
                .created(created.plusDays(1))
                .build();

        List<Comment> commentList = List.of(commentOne, commentTwo);

        when(commentRepository.findCommentsByItemId(anyInt()))
                .thenReturn(commentList);

        Booking lastBookingForOneItem = Booking.builder()
                .id(1)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(itemOne)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBookingForOneItem = Booking.builder()
                .id(2)
                .start(created.plusDays(1))
                .end(created.plusDays(2))
                .item(itemOne)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking lastBookingForTwoItem = Booking.builder()
                .id(3)
                .start(created.minusMonths(5))
                .end(created.minusMonths(4))
                .item(itemTwo)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        Booking nextBookingForTwoItem = Booking.builder()
                .id(4)
                .start(created.plusDays(2))
                .end(created.plusDays(3))
                .item(itemTwo)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        List<Booking> bookings = List.of(lastBookingForOneItem, lastBookingForTwoItem, nextBookingForOneItem,
                nextBookingForTwoItem);
        when(bookingRepository.findAllOwnerBookings(anyInt()))
                .thenReturn(bookings);

        itemRsList = itemService.getAllItemWithPagination(2, 0, 10);
        assertThat(itemRsList, is(notNullValue()));

        when(bookingRepository.findPastOwnerBookings(anyInt(), anyInt(),any()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findFutureOwnerBookings(anyInt(), anyInt(), any()))
                .thenReturn(Collections.emptyList());

        itemRsList = itemService.getAllItemWithPagination(2, 0, 10);
        assertThat(itemRsList, is(notNullValue()));

        Item item2 = Item.builder()
                .id(2)
                .name("name2")
                .description("description2")
                .available(true)
                .owner(owner)
                .build();

        items.add(item2);
        when(commentRepository.findCommentsByItemId(anyInt()))
                .thenReturn(Collections.emptyList());

        itemRsList = itemService.getAllItemWithPagination(2, 0, 10);
        assertThat(itemRsList, is(notNullValue()));
    }

    @Test
    void throwPaginationException() {
        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));
        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.getAllItemWithPagination(2, -1, 10));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.getAllItemWithPagination(2, 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.searchItemByTextWithPagination("text", -1, 10));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> itemService.searchItemByTextWithPagination("text", 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));
    }

    @Test
    void searchByText() {
        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(owner));

        List<ItemDtoRs> itemDtoRsList = itemService.searchItemByTextWithPagination("", 0, 10);
        Assertions.assertTrue(itemDtoRsList.isEmpty());

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        itemDtoRsList = itemService.searchItemByTextWithPagination("text", 0, 10);
        Assertions.assertTrue(itemDtoRsList.isEmpty());

        List<Item> items = List.of(item);

        when(itemRepository.getItemByText(anyString(), any()))
                .thenReturn(items);
        itemDtoRsList = itemService.searchItemByTextWithPagination("description", 0, 10);
        assertThat(itemDtoRsList, is(notNullValue()));
    }

    @Test
    void commentCreate() {
        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3))
                .thenReturn(Optional.of(booker));

        LocalDateTime created = LocalDateTime.now();

        when(bookingRepository
                .countAllByItemIdAndBookerIdAndEndBefore(anyInt(), anyInt(), any())
        ).thenReturn(1);

        Comment comment = Comment.builder()
                .id(1)
                .text("text")
                .item(item)
                .author(booker)
                .created(created)
                .build();
        when(commentRepository.save(any()))
                .thenReturn(comment);
        when(userService.isValidId(anyInt()))
                .thenReturn(true);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        CommentDtoRq commentDtoRq = CommentDtoRq.builder()
                .text("comment")
                .build();
        CommentDtoRs commentDtoRs = itemService.createComment(commentDtoRq, 3, 1);
        assertThat(commentDtoRs, is(notNullValue()));
    }

    @Test
    void editForbiddenException() {
        ItemUpdateDtoRq itemDtoRq = ItemUpdateDtoRq.builder()
                .name("nameUpdated")
                .description("descriptionUpdated")
                .build();

        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        EditForbiddenException forbiddenException;
        forbiddenException = Assertions.assertThrows(EditForbiddenException.class,
                () -> itemService.update(1, 1, itemDtoRq));
        assertThat(forbiddenException.getMessage(), is("Edit is forbidden for user with id 1"));
    }

    @Test
    void twoItemsWithBroForbiddenException() {
        ItemUpdateDtoRq itemDtoRq = ItemUpdateDtoRq.builder()
                .name("nameUpdated")
                .description("descriptionUpdated")
                .build();

        User owner = User.builder()
                .id(2)
                .name("user2")
                .email("user2@email.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        EditForbiddenException forbiddenException;
        forbiddenException = Assertions.assertThrows(EditForbiddenException.class,
                () -> itemService.update(1, 1, itemDtoRq));
        assertThat(forbiddenException.getMessage(), is("Edit is forbidden for user with id 1"));
    }
}
