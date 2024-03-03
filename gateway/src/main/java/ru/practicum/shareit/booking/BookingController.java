package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping()
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                                @Valid @RequestBody BookingSaveDtoRq bookingDto) {
        log.info("Получен запрос POST /bookings — на создание бронирования");
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer bookingId,
                                      @RequestParam Boolean approved) {
        log.info("Получен запрос PATCH /bookings/{bookingId}?approved={approved} — подтверждение или отклонение " +
                "запроса на бронирование");
        return bookingClient.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable Integer bookingId) {
        log.info("Получен запрос GET /bookings/{bookingId} — получить информацию о бронировании");
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllForBooker(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(required = false, defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} пользователя с id {}", state, userId);
        return bookingClient.get(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllForOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        log.info("поступил запрос на получение списка бронирований со статусом {} для вещей пользователя с id {}",
                state, userId);
        return bookingClient.getByOwner(userId, state, from, size);
    }
}
