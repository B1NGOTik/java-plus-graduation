package ru.practicum.ewm.main.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.model.comment.CommentStatus;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.main.service.comment.CommentService;

import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @DeleteMapping("/admin/comments/{commentId}")
    public void removeComment(@PathVariable Long commentId) {
        log.info("Admin: удаление комментария с ID = {}", commentId);
        commentService.adminRemoveCommentById(commentId);
    }

    @GetMapping("/admin/comments")
    public List<CommentFullDto> findAllComments(@RequestParam(required = false) Long userId,
                                                @RequestParam(required = false) Long eventId,
                                                @RequestParam(required = false) CommentStatus status,
                                                @RequestParam(required = false) LocalDateTime rangeStart,
                                                @RequestParam(required = false) LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0") Long from,
                                                @RequestParam(defaultValue = "10") Long size,
                                                @RequestParam(defaultValue = "desc") String sort) {
        log.info("Admin: запрос на получение комментариев");
        FindAllCommentsParams params = FindAllCommentsParams.builder()
                .userId(userId)
                .eventId(eventId)
                .status(status)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .sort(sort)
                .build();
        return commentService.findAllComments(params);
    }

    @PatchMapping("/admin/comments/{commentId}")
    public CommentFullDto moderateComment(@PathVariable Long commentId, @RequestParam(required = true) CommentStatus status) {
        log.info("Admin: модерация комментария с ID = {}, новый статус = {}", commentId, status);
        return commentService.moderateComment(commentId, status);
    }
}
