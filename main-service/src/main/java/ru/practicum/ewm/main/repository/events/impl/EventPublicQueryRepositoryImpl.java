package ru.practicum.ewm.main.repository.events.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.QEvents;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.events.params.PublicEventSearchParams;
import ru.practicum.ewm.main.repository.events.EventPublicQueryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventPublicQueryRepositoryImpl implements EventPublicQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Events> findPublicEvents(PublicEventSearchParams params, Pageable pageable) {
        QEvents events = QEvents.events;

        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(events.state.eq(EventState.PUBLISHED));
        if (StringUtils.hasText(params.getText())) {
            String pattern = "%" + params.getText().toLowerCase() + "%";
            predicate.and(events.annotation.lower().like(pattern))
                    .or(events.description.lower().like(pattern));
        }
        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            predicate.and(events.category.id.in(params.getCategories()));
        }

        if (params.getPaid() != null) {
            predicate.and(events.paid.eq(params.getPaid()));
        }

        if (params.getRangeStart() != null && params.getRangeEnd() != null) {
            predicate.and(events.eventDate.after(LocalDateTime.now()));
        } else {
            if (params.getRangeStart() != null) {
                predicate.and(events.eventDate.goe(params.getRangeStart()));
            }
            if (params.getRangeEnd() != null) {
                predicate.and(events.eventDate.loe(params.getRangeEnd()));
            }
        }

        JPAQuery<Events> query = queryFactory
                .selectFrom(events)
                .where(predicate);

        if ("EVENT_DATE".equalsIgnoreCase(params.getSort()) || params.getSort() == null) {
            query.orderBy(events.eventDate.asc());
        } else {
            query.orderBy(events.id.asc());
        }

        JPAQuery<Long> countQuery = queryFactory
                .select(events.count())
                .from(events)
                .where(predicate);

        long total = countQuery.fetchOne();

        List<Events> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
