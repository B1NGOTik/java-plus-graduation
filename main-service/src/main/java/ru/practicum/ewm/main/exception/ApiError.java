package ru.practicum.ewm.main.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    HttpStatus status;

    String reason;

    String message;

    LocalDateTime timestamp;
}
