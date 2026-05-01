package ru.yandex.practicum.service.events;

import jakarta.servlet.http.HttpServletRequest;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;

import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getEvents(PublicEventSearchParams params,
                                  HttpServletRequest request);

    EventFullDto getById(Long eventId,
                         HttpServletRequest request);

    EventFullDto getById(Long eventId);
}
