package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponse extends RuntimeException {
    private final int code;
    private final String error;

    public ErrorResponse(int code, String error) {
        super();
        this.code = code;
        this.error = error;
    }
}
