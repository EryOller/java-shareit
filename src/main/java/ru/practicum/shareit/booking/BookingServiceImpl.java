package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRs;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnavailableBookingException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.pagination_manager.PaginationManager;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.getBookingStatus;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDtoRs save(int userId, BookingSaveDtoRq bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
        || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BadRequestException("Не верный порядок дат");
        }
        if (itemService.isValid(bookingDto.getItemId()) &&
                itemService.findItemById(bookingDto.getItemId()).getOwner().getId() != userId) {
            if (itemService.isAvailableItem(userId, bookingDto.getItemId())   /*itemService.getItemById(userId, bookingDto.getItemId()).getAvailable()*/) {
                Booking booking = bookingMapper.toBooking(bookingDto);
                booking.setBooker(userService.findUserById(userId));
                booking.setStatus(BookingStatus.WAITING);
                booking.setItem(itemService.findItemById(bookingDto.getItemId()));
                return bookingMapper.toBookingDtoRs(bookingRepository.save(booking));
            } else {
                throw new UnavailableBookingException("Бронирование вещи с id " + bookingDto.getItemId()
                        + " не доступно!");
            }
        } else {
            throw new NotFoundException("Вещь с идентификатором " + bookingDto.getItemId() + " не найдена");
        }
    }

    @Override
    public BookingDtoRs approve(int userId, int bookingId, boolean isApprove) {
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (booking.getItem().getOwner().getId() == userId) {
                booking.setStatus(getBookingStatus(isApprove));
                return bookingMapper.toBookingDtoRs(bookingRepository.save(booking));
            } else {
                throw new NotFoundException("Пользователь с идентификатором " + userId
                        + " не может подтверждать или отклонять пронирование " + booking.getId());
            }
        } else {
            throw new BadRequestException("Пользователь с идентификатором " + userId
                    + " не может подтверждать или отклонять пронирование " + booking.getId());
        }
    }

    @Override
    public BookingDtoRs getBookingById(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id " + bookingId + " not found"));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return bookingMapper.toBookingDtoRs(bookingRepository.findById(bookingId).get());
        } else {
            throw new NotFoundException("Пользователь с идентификатором " + userId
                    + " не может редактировать бронирование " + booking.getId());
        }
    }

    private List<Booking> findBookingsForBooking(String state, int id, PageRequest pageReq) {
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(id, pageReq);
                return bookings;
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;
                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(id, status, pageReq);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(id, LocalDateTime.now(), pageReq);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now(), pageReq);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookerBookings(id, LocalDateTime.now(), pageReq);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return bookings;
    }

    @Override
    public List<BookingDtoRs> getBookingListWithPagination(int userId, String state, int from, int size) {

        if (userService.isValidId(userId)) {
            PageRequest pageReq = PaginationManager.form(from, size, Sort.Direction.DESC, "start");
            return findBookingsForBooking(state, userId, pageReq)
                    .stream()
                    .map(bookingMapper::toBookingDtoRs)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
    }

    @Override
    public List<BookingDtoRs> getByOwner(int userId, String state, int from, int size) {
        if (userService.isValidId(userId)) {
            PageRequest pageReq = PaginationManager.form(from, size, Sort.Direction.DESC, "start");
            return findBookingsForOwner(state, userId, pageReq)
                    .stream()
                    .map(bookingMapper::toBookingDtoRs)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
    }

    private List<Booking> findBookingsForOwner(String state, int id, PageRequest pageReq) {
        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(id, pageReq);
                return bookings;
            case "WAITING":
            case "APPROVED":
            case "REJECTED":
            case "CANCELED":
                BookingStatus status = null;

                for (BookingStatus value : BookingStatus.values()) {
                    if (value.name().equals(state)) {
                        status = value;
                    }
                }
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(id, status, pageReq);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(id, LocalDateTime.now(),
                        pageReq);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(id, LocalDateTime.now(),
                        pageReq);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentOwnerBookings(id, LocalDateTime.now(), pageReq);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return bookings;
    }
}
