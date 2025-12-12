package ru.practicum.server.exceptions;

public class UnavailableItemException extends RuntimeException {
    public UnavailableItemException(String message) {
        super(message);
    }
}
