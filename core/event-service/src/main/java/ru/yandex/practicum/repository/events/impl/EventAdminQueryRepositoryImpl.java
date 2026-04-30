package ru.yandex.practicum.repository.events.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.event.events.enums.EventState;
import ru.yandex.practicum.event.events.params.AdminEventSearchParams;
import ru.yandex.practicum.model.Events;
import ru.yandex.practicum.model.QEvents;
import ru.yandex.practicum.repository.events.EventAdminQueryRepository;

import java.util.List;

import static ru.yandex.practicum.model.QEvents.events;

@Repository
@RequiredArgsConstructor
public class EventAdminQueryRepositoryImpl implements EventAdminQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Events> findAdminEvents(AdminEventSearchParams params, Pageable pageable) {
        QEvents events = QEvents.events;

        BooleanBuilder predicate = new BooleanBuilder();

        if (params.getUsers() != null && !params.getUsers().isEmpty()) {
            predicate.and(events.initiatorId.in(params.getUsers()));
        }
        if (params.getStates() != null && !params.getStates().isEmpty()) {
            List<EventState> statesEnum = params.getStates().stream()
                    .map(String::toUpperCase)
                    .map(EventState::valueOf)
                    .toList();
            predicate.and(events.state.in(statesEnum));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            predicate.and(events.category.id.in(params.getCategories()));
        }

        if (params.getRangeStart() != null) {
            predicate.and(events.eventDate.goe(params.getRangeStart()));
        }
        if (params.getRangeEnd() != null) {
            predicate.and(events.eventDate.loe(params.getRangeEnd()));
        }
        return queryFactory
                .selectFrom(events)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
