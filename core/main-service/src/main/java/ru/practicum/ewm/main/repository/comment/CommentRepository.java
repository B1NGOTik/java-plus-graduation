package ru.practicum.ewm.main.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, QCommentRepository {

    void deleteCommentsByEventId(Long eventId);

}
