package ru.practicum.ewm.main.repository.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.model.events.Events;

public interface EventsRepository extends JpaRepository<Events, Long>,
        EventPublicQueryRepository, EventAdminQueryRepository {

    Page<Events> findAllByInitiatorId(Long initiatorId, Pageable pageable);
}
