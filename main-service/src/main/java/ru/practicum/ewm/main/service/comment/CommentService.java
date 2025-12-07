package ru.practicum.ewm.main.service.comment;

import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.model.comment.dto.NewCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    List<CommentShortDto> findAllCommentsForEvent(String sort, Long from, Long size);

    CommentFullDto findCommentById(Long eventId, Long commentId);

    CommentFullDto addComment(Long eventId, Long authorId, NewCommentRequest comment);

    List<CommentShortDto> findCommentsByAuthorId(Long authorId, Long eventId, Long from, Long size, String sort);

    Void removeCommentById(Long userId, Long commentId);

    List<CommentFullDto> findAllComments(Long eventId, Long userId, CommentStatus status,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Long from, Long size);

    CommentFullDto moderateComment(Long commentId, CommentStatus status);

    Void adminRemoveCommentById(Long commentId);
}
