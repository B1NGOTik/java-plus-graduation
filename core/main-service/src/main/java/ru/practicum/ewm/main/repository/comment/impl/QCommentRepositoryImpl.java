package ru.practicum.ewm.main.repository.comment.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.controller.comment.params.FindAllCommentsParams;
import ru.practicum.ewm.main.model.comment.Comment;
import ru.practicum.ewm.main.model.comment.QComment;
import ru.practicum.ewm.main.repository.comment.QCommentRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QCommentRepositoryImpl implements QCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Comment> findAllComments(FindAllCommentsParams params) {
        QComment comment = QComment.comment;

        BooleanBuilder predicate = new BooleanBuilder();

        if (params.getEventId() != null) {
            predicate.and(comment.event.id.eq(params.getEventId()));
        }

        if (params.getUserId() != null) {
            predicate.and(comment.author.id.eq(params.getUserId()));
        }

        if (params.getStatus() != null) {
            predicate.and(comment.status.eq(params.getStatus()));
        }

        if (params.getRangeStart() != null) {
            predicate.and(comment.createdOn.goe(params.getRangeStart()));
        }

        if (params.getRangeEnd() != null) {
            predicate.and(comment.createdOn.loe(params.getRangeEnd()));
        }

        OrderSpecifier<?> orderSpecifier;
        if ("asc".equalsIgnoreCase(params.getSort())) {
            orderSpecifier = new OrderSpecifier<>(Order.ASC, comment.createdOn);
        } else {
            orderSpecifier = new OrderSpecifier<>(Order.DESC, comment.createdOn);
        }


        JPAQuery<Comment> query = queryFactory
                .selectFrom(comment)
                .where(predicate)
                .orderBy(orderSpecifier);

        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(predicate);

        long total = countQuery.fetchOne();

        List<Comment> content = query
                .offset(params.getFrom() != null ? params.getFrom() : 0)
                .limit(params.getSize() != null ? params.getSize() : 10)
                .fetch();

        Pageable pageable = Pageable.ofSize(params.getSize() != null ? Math.toIntExact(params.getSize()) : 10)
                .withPage(params.getFrom() != null ? Math.toIntExact(params.getFrom() /
                        (params.getSize() != null ? params.getSize() : 10)) : 0);

        return new PageImpl<>(content, pageable, total);
    }
}