package ru.yandex.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.event.events.*;
import ru.yandex.practicum.event.events.params.AdminEventSearchParams;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.service.events.EventAuthorizedService;
import ru.yandex.practicum.service.events.EventPublicService;
import ru.yandex.practicum.service.events.EventsAdminService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class EventController implements EventOperations {
    private final EventsAdminService adminService;
    private final EventAuthorizedService authorizedService;
    private final EventPublicService publicService;

    @Override
    public List<EventFullDto> getEvents(AdminEventSearchParams params) {
        return adminService.getEvents(params);
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        return adminService.updateEvent(eventId, updateRequest);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        return authorizedService.getUserEvents(userId, from, size);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        return authorizedService.createEvent(userId, newEventDto);
    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        return authorizedService.updateUserEvent(userId, eventId, updateRequest);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        return authorizedService.getUserEvent(userId, eventId);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        return authorizedService.findEventRequests(userId, eventId);
    }

    @Override
    public EventRequestStatusUpdateResult rejectingRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        return authorizedService.rejectingRequest(userId, eventId, updateRequest);
    }

    @Override
    public EventFullDto getByIdPublic(Long id, HttpServletRequest request) {
        return publicService.getById(id, request);
    }

    @Override
    public EventFullDto getById(Long id) {
        return publicService.getById(id);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@ModelAttribute @Valid PublicEventSearchParams params,
                                         HttpServletRequest request) {
        log.info("Зашли в метод контроллера для получения событий с параметрами");
        return publicService.getEvents(params, request);
    }
}
