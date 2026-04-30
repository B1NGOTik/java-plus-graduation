package ru.yandex.practicum.event.events;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.events.params.AdminEventSearchParams;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "event-service")
public interface EventOperations {
    @GetMapping("admin/events/{id}")
    public EventFullDto getById(@PathVariable Long id);

    @GetMapping("/admin/events")
    public List<EventFullDto> getEvents(@Valid @ModelAttribute AdminEventSearchParams params);

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateRequest);

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size);

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto);

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateRequest);

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId);

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId);

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult rejectingRequest(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest);

    @GetMapping("/events/{id}")
    public EventFullDto getByIdPublic(@PathVariable Long id, HttpServletRequest request);

    /*@GetMapping("/events")
    public List<EventShortDto> getEvents(@ModelAttribute @Valid PublicEventSearchParams params,
                                         HttpServletRequest request);*/
}
