package ru.practicum.ewm.main.service.comment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.comment.CommentFullDtoMapper;
import ru.practicum.ewm.main.mapper.comment.CommentShortDtoMapper;
import ru.practicum.ewm.main.mapper.comment.NewCommentRequestMapper;
import ru.practicum.ewm.main.model.comment.Comment;
import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.model.comment.dto.NewCommentRequest;
import ru.practicum.ewm.main.repository.comment.CommentRepository;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    @Override
    public List<CommentShortDto> findAllCommentsForEvent(FindAllCommentsParams params) {
        return commentRepository.findAllComments(params).getContent()
                .stream()
                .map(CommentShortDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto findCommentById(Long eventId, Long commentId) {
        return CommentFullDtoMapper.toDto(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден")));
    }

    @Override
    public CommentFullDto addComment(Long eventId, Long authorId, NewCommentRequest comment) {
        Comment newComment = NewCommentRequestMapper.toEntity(comment);
        newComment.setAuthor(userRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        newComment.setEvent(eventsRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено")));
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
    public void  removeCommentById(Long userId, Long commentId) {
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
        Comment updatingComment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        updatingComment.setStatus(status);
        return CommentFullDtoMapper.toDto(commentRepository.save(updatingComment));
    }

    @Override
    public void adminRemoveCommentById(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
