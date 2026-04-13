package ru.practicum.ewm.main.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundHandler(NotFoundException e, HttpServletRequest request) {
        log.warn("404 NOT_FOUND: {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conflictHandler(ConflictException e,HttpServletRequest request) {
        log.warn("409 CONFLICT: {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError validationHandler(ValidationException e,HttpServletRequest request) {
        log.warn("400 BAD REQUEST: {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError otherHandler(RuntimeException e,HttpServletRequest request) {
        log.error("500 INTERNAL_SERVER_ERROR: {} {} -> {}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage(),
                e);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", e.getMessage(), LocalDateTime.now());
    }
}
