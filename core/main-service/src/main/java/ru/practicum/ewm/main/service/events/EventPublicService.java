package ru.practicum.ewm.main.service.events;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.params.PublicEventSearchParams;

import java.util.List;

public interface EventPublicService {

    List<EventShortDto> getEvents(PublicEventSearchParams params,
                                  HttpServletRequest request);

    EventFullDto getById(Long eventId,
                         HttpServletRequest request);
}
