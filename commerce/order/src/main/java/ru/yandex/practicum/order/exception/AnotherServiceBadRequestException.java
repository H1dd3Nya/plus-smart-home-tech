package ru.yandex.practicum.order.exception;

public class AnotherServiceBadRequestException extends RuntimeException {
    public AnotherServiceBadRequestException(String message) {
        super(message);
    }
}
