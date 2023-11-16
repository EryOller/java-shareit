package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EditForbiddenException extends RuntimeException {
    public EditForbiddenException(String message) {
        super(message);
    }
}
