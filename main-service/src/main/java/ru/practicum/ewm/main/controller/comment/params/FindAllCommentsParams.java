package ru.practicum.ewm.main.controller.comment.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.main.model.comment.CommentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class FindAllCommentsParams {
    Long eventId;

    Long userId;

    CommentStatus status;

    LocalDateTime rangeStart;

    LocalDateTime rangeEnd;

    Long from;

    Long size;

    String sort;
}
