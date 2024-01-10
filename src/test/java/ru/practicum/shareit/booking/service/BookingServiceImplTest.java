package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRs;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.PaginationException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    @Autowired
    private final BookingServiceImpl bookingService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final ItemService itemService;

    @MockBean
    private final UserService userService;

    @MockBean
    private final ItemMapper itemMapper;

    @MockBean
    private final BookingRepository bookingRepository;

    @Test
    void create() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingSaveDtoRq bookingDto = BookingSaveDtoRq.builder()
                .start(start)
                .end(end)
                .itemId(1)
                .build();

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();
        when(userRepository.findById(3))
                .thenReturn(Optional.of(booker));

        Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(itemService.isValid(anyInt()))
                .thenReturn(true);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(itemService.isAvailableItem(anyInt(), anyInt()))
                .thenReturn(true);

        BookingDtoRs bookingInfoDto = bookingService.save(3, bookingDto);
        assertThat(bookingInfoDto, is(notNullValue()));
    }

    @Test
    void throwItemNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingSaveDtoRq bookingDto = BookingSaveDtoRq.builder()
                .start(start)
                .end(end)
                .itemId(1)
                .build();

        EntityNotFoundException invalidItemIdException;

        when(itemRepository.findById(1))
                .thenReturn(Optional.empty());
        invalidItemIdException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.save(3, bookingDto));
        assertThat(invalidItemIdException.getMessage(), is("Вещь с идентификатором 1 не найдена"));
    }

    @Test
    void throwNotAvailableException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingSaveDtoRq bookingDto = BookingSaveDtoRq.builder()
                .start(start)
                .end(end)
                .itemId(1)
                .build();

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(false)
                .owner(owner)
                .build();

        when(itemService.isValid(anyInt()))
                .thenReturn(true);
        when(itemService.findItemById(anyInt()))
                .thenReturn(item);
        when(itemService.isAvailableItem(anyInt(), anyInt()))
                .thenReturn(false);

        UnavailableBookingException unavailableBookingException;
        unavailableBookingException = Assertions.assertThrows(UnavailableBookingException.class,
                () -> bookingService.save(3, bookingDto));
        assertThat(unavailableBookingException.getMessage(), is("Бронирование вещи с id 1 не доступно!"));
    }

    @Test
    void throwInvalidDateTimeException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingSaveDtoRq bookingDto = BookingSaveDtoRq.builder()
                .start(end)
                .end(start)
                .itemId(1)
                .build();

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        BadRequestException badRequestException;
        badRequestException = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.save(3, bookingDto));
        assertThat(badRequestException.getMessage(), is("Не верный порядок дат"));
    }

    @Test
    void throwUserNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingSaveDtoRq bookingDto = BookingSaveDtoRq.builder()
                .start(start)
                .end(end)
                .itemId(1)
                .build();

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
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
                .thenReturn(Optional.empty());

        EntityNotFoundException notFoundException;
        notFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.save(3, bookingDto));
        assertThat(notFoundException.getMessage(), is("Вещь с идентификатором 1 не найдена"));

        when(userRepository.findById(1))
                .thenReturn(Optional.of(owner));

        notFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.save(1, bookingDto));
        assertThat(notFoundException.getMessage(), is("Вещь с идентификатором 1 не найдена"));

        Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));

        notFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.approve(3, 1, true));
        assertThat(notFoundException.getMessage(),
                is("Пользователь с идентификатором 3 не может подтверждать или отклонять пронирование 1"));

        notFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingById(2, 1));
        assertThat(notFoundException.getMessage(),
                is("Пользователь с идентификатором 2 не может редактировать бронирование 1"));

        when(userRepository.findById(2))
                .thenReturn(Optional.empty());

        notFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBookingListWithPagination(2, "ALL", 0, 10));
        assertThat(notFoundException.getMessage(), is("Пользователь с идентификатором 2 не найден"));

        notFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.getByOwner(2, "ALL", 0, 10));
        assertThat(notFoundException.getMessage(), is("Пользователь с идентификатором 2 не найден"));
    }

    @Test
    void approveBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDtoRs bookingDtoRs = bookingService.approve(1, 1, true);
        assertThat(bookingDtoRs, is(notNullValue()));

        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setStatus(BookingStatus.APPROVED);
                            return invoc;
                        }
                );

        bookingDtoRs = bookingService.approve(1, 1, true);
        assertThat(bookingDtoRs, is(notNullValue()));

        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(
                        invocation -> {
                            Booking invoc = invocation.getArgument(0, Booking.class);
                            invoc.setStatus(BookingStatus.REJECTED);
                            return invoc;
                        }
                );
        bookingDtoRs = bookingService.approve(1, 1, false);
        assertThat(bookingDtoRs, is(notNullValue()));
        Assertions.assertEquals(bookingDtoRs.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void throwBookingNotFoundException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User booker = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
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

        Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));

        EntityNotFoundException bookingNotFoundException;

        bookingNotFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.approve(1, 1, true));
        assertThat(bookingNotFoundException.getMessage(),
                is("Пользователь с идентификатором 1 не может подтверждать или отклонять пронирование 1"));
    }

    @Test
    void throwInvalidStatusException() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        final Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));

        BadRequestException invalidStatusException;

        invalidStatusException = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.approve(1, 1, true));
        assertThat(invalidStatusException.getMessage(),
                is("Пользователь с идентификатором 1 не может подтверждать или отклонять пронирование 1"));

        booking.setStatus(BookingStatus.REJECTED);
        invalidStatusException = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.approve(1, 1, true));
        assertThat(invalidStatusException.getMessage(),
                is("Пользователь с идентификатором 1 не может подтверждать или отклонять пронирование 1"));

        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        invalidStatusException = Assertions.assertThrows(BadRequestException.class,
                () -> bookingService.getBookingListWithPagination(1, "value", 0, 10));
        assertThat(invalidStatusException.getMessage(), is("Unknown state: value"));
    }

    @Test
    void getBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        final Booking booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findById(1))
                .thenReturn(Optional.of(booking));

        BookingDtoRs bookingDtoRs = bookingService.getBookingById(1, 1);
        assertThat(bookingDtoRs, is(notNullValue()));

        bookingDtoRs = bookingService.getBookingById(3, 1);
        assertThat(bookingDtoRs, is(notNullValue()));
    }

    @Test
    void getAllByBookerAndStatus() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userRepository.findById(3))
                .thenReturn(Optional.of(booker));

        Booking booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(booking1));

        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        List<BookingDtoRs> bookingDtoRsList = bookingService
                .getBookingListWithPagination(3, "ALL", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().minusDays(2);
        end = LocalDateTime.now().minusDays(1);
        Booking booking2 = Booking.builder()
                .id(2)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(booking2));

        bookingDtoRsList = bookingService.getBookingListWithPagination(3, "PAST", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        Booking booking3 = Booking.builder()
                .id(3)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(booking3));

        bookingDtoRsList = bookingService.getBookingListWithPagination(3, "FUTURE", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().minusDays(1);
        end = LocalDateTime.now().plusDays(2);
        Booking booking4 = Booking.builder()
                .id(4)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findCurrentBookerBookings(anyInt(), any(), any()))
                .thenReturn(List.of(booking4));

        bookingDtoRsList = bookingService.getBookingListWithPagination(3, "CURRENT", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        Booking booking5 = Booking.builder()
                .id(5)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(any(Integer.class), any(BookingStatus.class),
                any(PageRequest.class)))
                .thenAnswer(
                        invocation -> {
                            Integer userId = invocation.getArgument(0, Integer.class);
                            BookingStatus status = invocation.getArgument(1, BookingStatus.class);
                            if (status.equals(BookingStatus.WAITING) && userId.equals(3)) {
                                booking5.setStatus(BookingStatus.WAITING);
                                return List.of(booking5);
                            }
                            if (status.equals(BookingStatus.REJECTED) && userId.equals(3)) {
                                booking5.setStatus(BookingStatus.REJECTED);
                                return List.of(booking5);
                            }
                            return Collections.emptyList();
                        }

                );

        User booker6 = User.builder()
                .id(6)
                .name("user6")
                .email("user6@email.com")
                .build();

        when(userRepository.findById(6))
                .thenReturn(Optional.of(booker6));

        bookingDtoRsList = bookingService.getBookingListWithPagination(3, "WAITING", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());
        assertThat(bookingDtoRsList.get(0).getStatus(), is(BookingStatus.WAITING));

        bookingDtoRsList = bookingService.getBookingListWithPagination(3, "REJECTED", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());
        assertThat(bookingDtoRsList.get(0).getStatus(), is(BookingStatus.REJECTED));

        bookingDtoRsList = bookingService.getBookingListWithPagination(6, "WAITING", 0, 10);
        Assertions.assertTrue(bookingDtoRsList.isEmpty());
    }

    @Test
    void throwPaginationException() {
        User user = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userRepository.findById(3))
                .thenReturn(Optional.of(user));
        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        PaginationException invalidPageParamsException;

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.getBookingListWithPagination(3, "ALL", -1, 10));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.getBookingListWithPagination(3, "ALL", 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.getByOwner(3, "ALL", -1, 10));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));

        invalidPageParamsException = Assertions.assertThrows(PaginationException.class,
                () -> bookingService.getByOwner(3, "ALL", 0, 0));
        assertThat(invalidPageParamsException.getMessage(), is("paging invalid"));
    }

    @Test
    void getByOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        User owner = User.builder()
                .id(1)
                .name("user1")
                .email("user1@email.com")
                .build();
        Item item = Item.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .owner(owner)
                .build();

        User booker = User.builder()
                .id(3)
                .name("user3")
                .email("user3@email.com")
                .build();

        when(userRepository.findById(1))
                .thenReturn(Optional.of(owner));

        Booking booking1 = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(userService.isValidId(anyInt()))
                .thenReturn(true);

        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyInt(), any()))
                .thenReturn(List.of(booking1));

        List<BookingDtoRs> bookingDtoRsList = bookingService.getByOwner(1, "ALL", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().minusDays(2);
        end = LocalDateTime.now().minusDays(1);
        Booking booking2 = Booking.builder()
                .id(2)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(booking2));

        bookingDtoRsList = bookingService.getByOwner(1, "PAST", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        Booking booking3 = Booking.builder()
                .id(3)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(booking3));
        bookingDtoRsList = bookingService.getByOwner(1, "FUTURE", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().minusDays(1);
        end = LocalDateTime.now().plusDays(2);
        Booking booking4 = Booking.builder()
                .id(4)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findCurrentOwnerBookings(anyInt(), any(), any()))
                .thenReturn(List.of(booking4));

        bookingDtoRsList = bookingService.getByOwner(1, "CURRENT", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);
        Booking booking5 = Booking.builder()
                .id(5)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(any(Integer.class), any(BookingStatus.class),
                any(PageRequest.class)))
                .thenAnswer(
                        invocation -> {
                            Integer userId = invocation.getArgument(0, Integer.class);
                            BookingStatus status = invocation.getArgument(1, BookingStatus.class);
                            if (status.equals(BookingStatus.WAITING) && userId.equals(1)) {
                                booking5.setStatus(BookingStatus.WAITING);
                                return List.of(booking5);
                            }
                            if (status.equals(BookingStatus.REJECTED) && userId.equals(1)) {
                                booking5.setStatus(BookingStatus.REJECTED);
                                return List.of(booking5);
                            }
                            return Collections.emptyList();
                        }
                );

        Booking booking6 = Booking.builder()
                .id(6)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(anyInt(), any(), any()))
                .thenReturn(List.of(booking6));

        bookingDtoRsList = bookingService.getByOwner(1, "WAITING", 0, 10);
        Assertions.assertFalse(bookingDtoRsList.isEmpty());
        assertThat(bookingDtoRsList.get(0).getStatus(), is(BookingStatus.WAITING));
    }
}
