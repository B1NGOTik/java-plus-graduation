package ru.practicum.ewm.main.mapper.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.model.comment.Comment;
import ru.practicum.ewm.main.model.comment.dto.CommentFullDto;

@UtilityClass
public class CommentFullDtoMapper {

    public static CommentFullDto toDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .status(comment.getStatus())
                .build();
    }
}