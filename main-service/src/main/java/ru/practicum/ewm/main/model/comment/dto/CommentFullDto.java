package ru.practicum.ewm.main.model.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentFullDto {
    Long id;
    EventShortDto event;
    UserShortDto author;
    LocalDateTime createdOn;
    CommentStatus status;
}