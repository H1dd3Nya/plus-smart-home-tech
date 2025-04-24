package ru.yandex.practicum.interactionapi.exception;

public class AnotherServiceNotFoundException extends RuntimeException {
    public AnotherServiceNotFoundException(String message) {
        super(message);
    }
}
