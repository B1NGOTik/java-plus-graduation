package ru.practicum.ewm.main.repository.comment;

import org.springframework.data.domain.Page;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.model.comment.Comment;

public interface QCommentRepository {

    Page<Comment> findAllComments(FindAllCommentsParams params);
}
