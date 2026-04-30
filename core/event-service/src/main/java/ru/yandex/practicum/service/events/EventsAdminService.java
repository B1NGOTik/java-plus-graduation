package ru.yandex.practicum.service.events;

import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.UpdateEventAdminRequest;
import ru.yandex.practicum.event.events.params.AdminEventSearchParams;

import java.util.List;

public interface EventsAdminService {

    List<EventFullDto> getEvents(AdminEventSearchParams params);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest);
}
