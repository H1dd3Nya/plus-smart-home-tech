package ru.yandex.practicum.interactionapi.exception;

public class AnotherServiceBadRequestException extends RuntimeException {
    public AnotherServiceBadRequestException(String message) {
        super(message);
    }
}
