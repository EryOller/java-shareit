package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingItemDtoRs {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int bookerId;
}
