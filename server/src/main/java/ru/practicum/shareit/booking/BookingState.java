package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

//    public static State validateState(String value) throws InvalidStatusException  {
//        try {
//            return State.valueOf(value);
//        } catch (RuntimeException exception) {
//            throw new InvalidStatusException("Unknown state: " + value);
//        }
//    }

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
