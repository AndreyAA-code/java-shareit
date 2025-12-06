package ru.practicum.server.exceptions;

public class StatusException extends RuntimeException {
    public StatusException(String message) {
        super(message);
    }
}
