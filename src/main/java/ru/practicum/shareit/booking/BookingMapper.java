package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.dto.BookingDtoRs;
import ru.practicum.shareit.booking.dto.BookingSaveDtoRq;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {
    Booking toBooking(BookingSaveDtoRq bookingDto);

    BookingDtoRs toBookingDtoRs(Booking booking);

    List<BookingDtoRs> toListBookingDtoRs(List<Booking> bookings);
}
