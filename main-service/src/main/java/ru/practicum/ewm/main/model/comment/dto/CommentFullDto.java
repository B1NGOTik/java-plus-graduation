package ru.practicum.ewm.main.model.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.model.comment.CommentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentFullDto {
    Long id;
    Long eventId;
    Long authorId;
    String text;
    LocalDateTime createdOn;
    CommentStatus status;
}