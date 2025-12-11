package ru.practicum.ewm.main.service.comment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.comment.CommentFullDtoMapper;
import ru.practicum.ewm.main.mapper.comment.CommentShortDtoMapper;
import ru.practicum.ewm.main.mapper.comment.NewCommentRequestMapper;
import ru.practicum.ewm.main.model.comment.Comment;
import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.model.comment.dto.NewCommentRequest;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.repository.comment.CommentRepository;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    @Override
    public List<CommentShortDto> findAllCommentsForEvent(FindAllCommentsParams params) {
        if (!eventsRepository.existsById(params.getEventId())) {
            throw new NotFoundException("Событие не найдено");
        }
        return commentRepository.findAllComments(params).getContent()
                .stream()
                .map(CommentShortDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto findCommentById(Long eventId, Long commentId) {
        if (!eventsRepository.existsById(eventId)) {
            throw new NotFoundException("Событие не найдено");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!comment.getStatus().equals(CommentStatus.PUBLISHED)) {
            throw new NotFoundException("Комментарий еще не опубликован");
        }
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ConflictException("Комментарий не относится к указанному событию");
        }
        return CommentFullDtoMapper.toDto(comment);
    }

    @Override
    public CommentFullDto addComment(Long eventId, Long authorId, NewCommentRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие еще не опубликовано");
        }
        Comment newComment = NewCommentRequestMapper.toEntity(request);
        newComment.setAuthor(author);
        newComment.setEvent(event);
        newComment.setStatus(CommentStatus.PENDING);
        return CommentFullDtoMapper.toDto(commentRepository.save(newComment));
    }

    @Override
    public List<CommentFullDto> findCommentsByAuthorId(FindAllCommentsParams params) {
        return commentRepository.findAllComments(params).getContent()
                .stream()
                .map(CommentFullDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeCommentById(Long userId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Пользователь не является автором комментария");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentFullDto> findAllComments(FindAllCommentsParams params) {
        return commentRepository.findAllComments(params).getContent()
                .stream()
                .map(CommentFullDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto moderateComment(Long commentId, CommentStatus status) {
        Comment updatingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        updatingComment.setStatus(status);
        return CommentFullDtoMapper.toDto(commentRepository.save(updatingComment));
    }

    @Override
    public void adminRemoveCommentById(Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
