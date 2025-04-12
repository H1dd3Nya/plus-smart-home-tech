package ru.yandex.practicum.error;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.error.model.ErrorResponse;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({NotAuthorizedUserException.class})
    public ErrorResponse handleAuthorizedUserException(MethodArgumentNotValidException e) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED,
                "Ошибка авторизации",
                e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Произошла непредвиденная ошибка.",
                e.getMessage()
        );
    }

    @ExceptionHandler({ValidationException.class, NoProductsInShoppingCartException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации",
                e.getMessage()
        );
    }
}
