package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDtoRs;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;

import java.util.List;

public interface BookingService {
    BookingDtoRs save(int userId, BookingSaveDtoRq bookingDto);

    BookingDtoRs approve(int userId, int bookingId, boolean isApprove);

    BookingDtoRs getBookingById(int userId, int bookingId);

    List<BookingDtoRs> getBookingListWithPagination(int userId, String state, int from, int size);

    List<BookingDtoRs> getByOwner(int userId, String state, int from, int size);
}
