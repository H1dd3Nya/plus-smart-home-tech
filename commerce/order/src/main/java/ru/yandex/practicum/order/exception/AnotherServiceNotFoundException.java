package ru.yandex.practicum.order.exception;

public class AnotherServiceNotFoundException extends RuntimeException {
    public AnotherServiceNotFoundException(String message) {
        super(message);
    }
}
