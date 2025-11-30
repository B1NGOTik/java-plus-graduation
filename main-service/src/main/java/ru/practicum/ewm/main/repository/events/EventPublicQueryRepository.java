package ru.practicum.ewm.main.repository.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.params.PublicEventSearchParams;

public interface EventPublicQueryRepository {

    Page<Events> findPublicEvents(PublicEventSearchParams params, Pageable pageable);
}
