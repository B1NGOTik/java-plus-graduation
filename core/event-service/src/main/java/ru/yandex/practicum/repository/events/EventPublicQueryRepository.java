package ru.yandex.practicum.repository.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;
import ru.yandex.practicum.model.Events;

public interface EventPublicQueryRepository {

    @EntityGraph(type = EntityGraph.EntityGraphType.LOAD,
            attributePaths = {"category", "location", "location"})
    Page<Events> findPublicEvents(PublicEventSearchParams params, Pageable pageable);
}
