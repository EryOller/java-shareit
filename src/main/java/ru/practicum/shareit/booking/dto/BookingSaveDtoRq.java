package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.validator.DateConstraint;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class BookingSaveDtoRq {
    @DateConstraint
    private LocalDateTime start;
    @DateConstraint
    private LocalDateTime end;
    private int itemId;

    @AssertTrue()
    private boolean isValidDate() {
        return start != null && end != null && start.isBefore(end);
    }
}
