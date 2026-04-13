package ru.practicum.ewm.main.service.events;

import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.model.events.params.AdminEventSearchParams;

import java.util.List;

public interface EventsAdminService {

    List<EventFullDto> getEvents(AdminEventSearchParams params);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest);
}
