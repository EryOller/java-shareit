package ru.practicum.shareit.booking.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateConstraint {
    String message() default "Booking end data no must be in past!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
