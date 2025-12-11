package ru.practicum.ewm.main.service.events.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.ValidationException;
import ru.practicum.ewm.main.mapper.events.EventsMapper;
import ru.practicum.ewm.main.model.category.Category;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.events.enums.StateActionAdminUpdateEvent;
import ru.practicum.ewm.main.model.events.params.AdminEventSearchParams;
import ru.practicum.ewm.main.repository.category.CategoryRepository;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.service.events.EventsAdminService;
import ru.practicum.ewm.main.service.request.ParticipationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class EventsAdminServiceImpl implements EventsAdminService {

    private final EventsMapper mapper;
    private final EventsRepository eventsRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestValidator requestValidator;

    @Override
    public List<EventFullDto> getEvents(AdminEventSearchParams params) {

        var pageable = params.toPageable();

        List<Events> found = eventsRepository.findAdminEvents(params, pageable);

        List<EventFullDto> dtos = found.stream()
                .map(mapper::toFullDto)
                .toList();

        requestValidator.fillConfirmedRequests(dtos);
        return dtos;
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) {

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено", eventId);
                    return new NotFoundException("Event with id=" + eventId + " not found");
                });

        mapper.updateEventFromAdminRequest(updateRequest, event);

        if (updateRequest.getEventDate() != null) {
            LocalDateTime newDate = updateRequest.getEventDate();
            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                log.warn("Нарушено ограничение по дате при обновлении события id={}, newDate={}",
                        eventId, newDate);
                throw new ValidationException(
                        "Field: eventDate. Error: должно содержать дату, которая еще не наступила."
                );
            }
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Category with id=" + updateRequest.getCategory() + " not found"));
            event.setCategory(category);
        }

        if (updateRequest.getLocation() != null && event.getLocation() != null) {
            event.getLocation().setLat(updateRequest.getLocation().lat());
            event.getLocation().setLon(updateRequest.getLocation().lon());
        }

        if (updateRequest.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Можно опубликовать только событие в статусе PENDING");
            }
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (updateRequest.getStateAction() == StateActionAdminUpdateEvent.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Нельзя отклонить уже опубликованное событие");
            }
            event.setState(EventState.CANCELED);
        }
        Events saved = eventsRepository.save(event);
        return mapper.toFullDto(saved);
    }


}
