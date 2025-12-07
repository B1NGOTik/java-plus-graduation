package ru.practicum.ewm.main.controller.events;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.model.events.params.AdminEventSearchParams;
import ru.practicum.ewm.main.service.events.EventsAdminService;

import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Slf4j
public class EventsAdminController {

    private final EventsAdminService eventsAdminService;

    @GetMapping
    public List<EventFullDto> getEvents(@Valid @ModelAttribute AdminEventSearchParams params) {
        log.info("GET /admin/events params={}", params);
        return eventsAdminService.getEvents(params);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        log.info("PATCH /admin/events/{} body={}", eventId, updateRequest);
        return eventsAdminService.updateEvent(eventId, updateRequest);
    }


}
