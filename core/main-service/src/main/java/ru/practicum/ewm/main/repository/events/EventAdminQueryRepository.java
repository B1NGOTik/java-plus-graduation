package ru.practicum.ewm.main.repository.events;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.params.AdminEventSearchParams;

import java.util.List;

public interface EventAdminQueryRepository {
    List<Events> findAdminEvents(AdminEventSearchParams params, Pageable pageable);
}
