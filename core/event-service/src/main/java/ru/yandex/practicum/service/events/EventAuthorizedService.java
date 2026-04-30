package ru.yandex.practicum.service.events;


import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.event.events.NewEventDto;
import ru.yandex.practicum.event.events.UpdateEventUserRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;

import java.util.List;


public interface EventAuthorizedService {
    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    EventFullDto getUserEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult rejectingRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);
}
