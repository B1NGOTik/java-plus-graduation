package ru.practicum.ewm.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.NewCommentRequest;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentAuthorizedController {
    private final CommentService commentService;

    @GetMapping("/users/{userId}/comments")
    public List<CommentFullDto> findAllUserComments(@PathVariable Long userId,
                                                    @RequestParam(required = false) Long eventId,
                                                    @RequestParam(defaultValue = "10") Long size,
                                                    @RequestParam(defaultValue = "0") Long from,
                                                    @RequestParam(defaultValue = "desc") String sort) {
        FindAllCommentsParams params = FindAllCommentsParams.builder()
                .userId(userId)
                .eventId(eventId)
                .size(size)
                .from(from)
                .sort(sort)
                .build();
        return commentService.findCommentsByAuthorId(params);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/events/{eventId}/comments")
    public CommentFullDto writeComment(@PathVariable Long eventId,
                                       @RequestParam Long userId,
                                       @RequestBody NewCommentRequest comment) {
        return commentService.addComment(eventId, userId, comment);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public void removeComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.removeCommentById(userId, commentId);
    }

}
