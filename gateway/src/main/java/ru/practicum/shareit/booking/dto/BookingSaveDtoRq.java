package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.validator.DateConstraint;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class BookingSaveDtoRq {
    @NotNull
    @DateConstraint
    private LocalDateTime start;
    @NotNull
    @DateConstraint
    private LocalDateTime end;
    private int itemId;

    public BookingSaveDtoRq(LocalDateTime start, LocalDateTime end, int itemId) {
        this.start = start;
        this.end = end;
        this.itemId = itemId;
    }
}