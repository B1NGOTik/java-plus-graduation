package ru.practicum.ewm.main.repository.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.params.PublicEventSearchParams;

public interface EventPublicQueryRepository {

    @EntityGraph(type = EntityGraph.EntityGraphType.LOAD,
            attributePaths = {"category", "location", "location"})
    Page<Events> findPublicEvents(PublicEventSearchParams params, Pageable pageable);
}
