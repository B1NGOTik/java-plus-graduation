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

    String ADMIN_PATH = "/admin/events";
    String USER_PATH = "/users/{userId}/events";
    String PUBLIC_PATH = "/events";

    @GetMapping(ADMIN_PATH + "/{id}")
    EventFullDto getById(@PathVariable Long id);

    @GetMapping(ADMIN_PATH)
    List<EventFullDto> getEvents(@Valid @ModelAttribute AdminEventSearchParams params);

    @PatchMapping(ADMIN_PATH + "/{eventId}")
    EventFullDto updateEvent(@PathVariable Long eventId,
                             @Valid @RequestBody UpdateEventAdminRequest updateRequest);

    @GetMapping(USER_PATH)
    List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size);

    @PostMapping(USER_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    EventFullDto createEvent(@PathVariable Long userId,
                             @Valid @RequestBody NewEventDto newEventDto);

    @PatchMapping(USER_PATH + "/{eventId}")
    EventFullDto updateUserEvent(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody UpdateEventUserRequest updateRequest);

    @GetMapping(USER_PATH + "/{eventId}")
    EventFullDto getUserEvent(@PathVariable Long userId,
                              @PathVariable Long eventId);

    @GetMapping(USER_PATH + "/{eventId}/requests")
    List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                              @PathVariable Long eventId);

    @PatchMapping(USER_PATH + "/{eventId}/requests")
    EventRequestStatusUpdateResult rejectingRequest(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest);

    @GetMapping(PUBLIC_PATH + "/{id}")
    EventFullDto getByIdPublic(@PathVariable Long id, HttpServletRequest request);

}