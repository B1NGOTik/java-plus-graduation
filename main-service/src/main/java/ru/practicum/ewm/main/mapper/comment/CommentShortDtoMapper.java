package ru.practicum.ewm.main.mapper.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.model.comment.Comment;
import ru.practicum.ewm.main.model.comment.dto.CommentShortDto;

@UtilityClass
public class CommentShortDtoMapper {
    public static CommentShortDto toDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}
