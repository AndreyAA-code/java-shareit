package ru.practicum.server.exceptions;

public class NoRightsException extends RuntimeException {
    public NoRightsException(String message) {
        super(message);
    }
}
