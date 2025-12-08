package ru.practicum.ewm.main.controller.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;
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
                                                     @RequestParam(defaultValue = "0") Long from) {
        FindAllCommentsParams params = FindAllCommentsParams.builder()
                .userId(userId)
                .eventId(eventId)
                .size(size)
                .from(from)
                .build();
        return commentService.findCommentsByAuthorId(params);
    }
    @PostMapping("/events/{eventId}/comments")
    public CommentFullDto writeComment(@PathVariable Long eventId,
                                       @RequestParam Long userId,
                                       @RequestBody NewCommentRequest comment) {
        return commentService.addComment(eventId, userId, comment);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    public void removeComment(@PathVariable Long userId, @PathVariable Long commentId){
        commentService.removeCommentById(userId, commentId);
    }

}
