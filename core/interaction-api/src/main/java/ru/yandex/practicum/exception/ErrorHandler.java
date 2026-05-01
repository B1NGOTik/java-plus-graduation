package ru.yandex.practicum.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiError> handleFeignException(FeignException e, HttpServletRequest request) {
        int status = e.status();
        HttpStatus httpStatus = HttpStatus.resolve(status);

        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message = e.getMessage();

        log.warn("{} from FeignClient: {} {} -> {}",
                status,
                request.getMethod(),
                request.getRequestURI(),
                message);

        ApiError apiError = new ApiError(
                httpStatus,
                getReasonPhrase(httpStatus),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity.status(httpStatus).body(apiError);
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

    private String getReasonPhrase(HttpStatus status) {
        return switch (status) {
            case CONFLICT -> "Integrity constraint has been violated.";
            case NOT_FOUND -> "The required object was not found.";
            case BAD_REQUEST -> "Incorrectly made request.";
            default -> "Internal error";
        };
    }
}
