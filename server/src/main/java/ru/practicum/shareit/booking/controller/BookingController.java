package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDtoRs;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping()
    public BookingDtoRs createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @Valid @RequestBody BookingSaveDtoRq bookingDto) {
        log.info("Получен запрос POST /bookings — на создание бронирования");
        return bookingService.save(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoRs updateBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer bookingId,
                                      @RequestParam Boolean approved) {
        log.info("Получен запрос PATCH /bookings/{bookingId}?approved={approved} — подтверждение или отклонение " +
                "запроса на бронирование");
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoRs getBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer bookingId) {
        log.info("Получен запрос GET /bookings/{bookingId} — получить информацию о бронировании");
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingDtoRs> getAllForBooker(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} пользователя с id {}", state, userId);
        return bookingService.getBookingListWithPagination(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoRs> getAllForOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} для вещей пользователя с id {}",
                state, userId);
        return bookingService.getByOwner(userId, state, from, size);
    }
}
