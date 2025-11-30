package ru.practicum.ewm.main.repository.events.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.QEvents;
import ru.practicum.ewm.main.model.events.params.AdminEventSearchParams;
import ru.practicum.ewm.main.repository.events.EventAdminQueryRepository;

@Repository
@RequiredArgsConstructor
public class EventAdminQueryRepositoryImpl implements EventAdminQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Events> findAdminEvents(AdminEventSearchParams params, Pageable pageable) {
        QEvents event = QEvents.events;

//        return new PageImpl<>(content, pageable, total);
        return null;
    }
}
