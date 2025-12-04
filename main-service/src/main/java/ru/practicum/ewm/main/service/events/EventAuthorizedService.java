package ru.practicum.ewm.main.service.events;

import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.dto.NewEventDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventUserRequest;

import java.util.List;


public interface EventAuthorizedService {
    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto getUserEvent(Long userId, Long eventId);

}
