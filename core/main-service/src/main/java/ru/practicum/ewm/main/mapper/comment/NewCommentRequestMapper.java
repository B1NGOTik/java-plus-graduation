package ru.practicum.ewm.main.mapper.comment;

import ru.practicum.ewm.main.model.comment.Comment;
import ru.practicum.ewm.main.model.comment.dto.NewCommentRequest;

import java.time.LocalDateTime;

public class NewCommentRequestMapper {
    public static Comment toEntity(NewCommentRequest comment) {
        return Comment.builder()
                .text(comment.getText())
                .createdOn(LocalDateTime.now())
                .build();
    }
}
