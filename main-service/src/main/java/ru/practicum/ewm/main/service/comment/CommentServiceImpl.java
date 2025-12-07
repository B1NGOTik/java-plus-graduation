package ru.practicum.ewm.main.service.comment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.model.comment.dto.NewCommentRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{
    @Override
    public List<CommentShortDto> findAllCommentsForEvent(String sort, Long from, Long size) {
        return List.of();
    }

    @Override
    public CommentFullDto findCommentById(Long eventId, Long commentId) {
        return null;
    }

    @Override
    public CommentFullDto addComment(Long eventId, Long authorId, NewCommentRequest comment) {
        return null;
    }

    @Override
    public List<CommentShortDto> findCommentsByAuthorId(Long authorId, Long eventId, Long from, Long size, String sort) {
        return List.of();
    }

    @Override
    public Void removeCommentById(Long userId, Long commentId) {
        return null;
    }

    @Override
    public List<CommentFullDto> findAllComments(Long eventId, Long userId, CommentStatus status, LocalDateTime rangeStart, LocalDateTime rangeEnd, Long from, Long size) {
        return List.of();
    }

    @Override
    public CommentFullDto moderateComment(Long commentId, CommentStatus status) {
        return null;
    }

    @Override
    public Void adminRemoveCommentById(Long commentId) {
        return null;
    }
}
