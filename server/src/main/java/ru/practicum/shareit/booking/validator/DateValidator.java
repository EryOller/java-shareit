package ru.practicum.shareit.booking.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<DateConstraint, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime date, ConstraintValidatorContext context) {
        return date != null && date.isAfter(LocalDateTime.now());
    }
}
