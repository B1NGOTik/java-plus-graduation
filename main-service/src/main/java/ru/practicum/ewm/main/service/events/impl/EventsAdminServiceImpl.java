package ru.practicum.ewm.main.service.events.impl;

import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.model.events.params.AdminEventSearchParams;
import ru.practicum.ewm.main.service.events.EventsAdminService;

import java.util.List;

public class EventsAdminServiceImpl implements EventsAdminService {


    @Override
    public List<EventFullDto> getEvents(AdminEventSearchParams params) {
        return List.of();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        return null;
    }
}
