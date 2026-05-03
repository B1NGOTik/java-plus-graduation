package ru.yandex.practicum.service.events.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.UpdateEventAdminRequest;
import ru.yandex.practicum.event.events.enums.EventState;
import ru.yandex.practicum.event.events.enums.StateActionAdminUpdateEvent;
import ru.yandex.practicum.event.events.params.AdminEventSearchParams;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.mapper.events.EventsMapper;
import ru.yandex.practicum.model.Category;
import ru.yandex.practicum.model.Events;
import ru.yandex.practicum.repository.CategoryRepository;
import ru.yandex.practicum.repository.events.EventsRepository;
import ru.yandex.practicum.request.RequestOperations;
import ru.yandex.practicum.service.events.EventsAdminService;
import ru.yandex.practicum.user.UserOperations;
import ru.yandex.practicum.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class EventsAdminServiceImpl implements EventsAdminService {

    private final EventsMapper mapper;
    private final EventsRepository eventsRepository;
    private final CategoryRepository categoryRepository;
    private final UserOperations userClient;
    private final RequestOperations requestClient;

    @Override
    public List<EventFullDto> getEvents(AdminEventSearchParams params) {

        var pageable = params.toPageable();

        List<Events> found = eventsRepository.findAdminEvents(params, pageable);

        return found.stream()
                .map(event -> {
                    UserShortDto initiator = getInitiator(event.getInitiatorId());
                    EventFullDto fullDto = mapper.toFullDto(event);
                    fullDto.setInitiator(initiator);
                    fullDto.setConfirmedRequests(requestClient.getConfirmedRequests(fullDto.getId()));
                    return fullDto;
                })
                .toList();
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
            log.info("Опубликовано событие с id: {}", eventId);
        } else if (updateRequest.getStateAction() == StateActionAdminUpdateEvent.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Нельзя отклонить уже опубликованное событие");
            }
            event.setState(EventState.CANCELED);
        }
        Events saved = eventsRepository.save(event);
        EventFullDto dto = mapper.toFullDto(saved);
        dto.setInitiator(getInitiator(saved.getInitiatorId()));
        return dto;
    }

    private UserShortDto getInitiator(Long initiatorId){
        return userClient.findShortDto(initiatorId);
    }
}
