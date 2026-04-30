package ru.yandex.practicum.repository.events;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.event.events.params.AdminEventSearchParams;
import ru.yandex.practicum.model.Events;

import java.util.List;

public interface EventAdminQueryRepository {
    List<Events> findAdminEvents(AdminEventSearchParams params, Pageable pageable);
}
