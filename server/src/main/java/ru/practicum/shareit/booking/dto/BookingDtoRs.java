package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.user.dto.UserDtoRs;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoRs {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDtoRs item;
    private UserDtoRs booker;
    private BookingStatus status;
}
