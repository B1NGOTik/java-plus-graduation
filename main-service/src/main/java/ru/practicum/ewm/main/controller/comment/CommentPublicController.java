package ru.practicum.ewm.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments/{commentId}")
    public CommentFullDto findCommentById(@PathVariable Long eventId, @PathVariable Long commentId) {
        return commentService.findCommentById(eventId, commentId);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentShortDto> findAllComments(@PathVariable Long eventId,
                                                 @RequestParam(defaultValue = "10") Long size,
                                                 @RequestParam(defaultValue = "0") Long from,
                                                 @RequestParam(defaultValue = "desc") String sort) {
        FindAllCommentsParams params = FindAllCommentsParams.builder()
                .eventId(eventId)
                .status(CommentStatus.PUBLISHED)
                .size(size)
                .from(from)
                .sort(sort)
                .build();
        return commentService.findAllCommentsForEvent(params);
    }
}
