package ru.practicum.ewm.main.controller.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.dto.NewEventDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.main.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.model.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.service.events.EventAuthorizedService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
public class EventsAuthorizedController {

    private final EventAuthorizedService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /users/{}/events from={} size={}", userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST /users/{}/events body={}", userId, newEventDto);
        return eventService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        log.info("PATCH /users/{}/events/{} body={}", userId, eventId, updateRequest);
        return eventService.updateUserEvent(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId
    ) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getUserEvent(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return eventService.findEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult rejectingRequest(@PathVariable Long userId,
                                                           @PathVariable Long eventId,
                                                           @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        log.info("PATCH /users/{}/events/{}/requests/{}", userId, eventId, updateRequest);
        return eventService.rejectingRequest(userId, eventId, updateRequest);
    }
}
