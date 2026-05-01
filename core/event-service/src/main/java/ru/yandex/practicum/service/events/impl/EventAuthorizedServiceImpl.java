package ru.yandex.practicum.service.events.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.event.events.NewEventDto;
import ru.yandex.practicum.event.events.UpdateEventUserRequest;
import ru.yandex.practicum.event.events.enums.EventState;
import ru.yandex.practicum.event.events.enums.StateActionUserUpdateEvent;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.mapper.events.EventsMapper;
import ru.yandex.practicum.model.Category;
import ru.yandex.practicum.model.Events;
import ru.yandex.practicum.model.Location;
import ru.yandex.practicum.repository.events.EventsRepository;
import ru.yandex.practicum.request.*;
import ru.yandex.practicum.service.category.CategoryService;
import ru.yandex.practicum.service.events.EventAuthorizedService;
import ru.yandex.practicum.service.location.LocationService;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserOperations;
import ru.yandex.practicum.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class EventAuthorizedServiceImpl implements EventAuthorizedService {

    private final EventsMapper mapper;
    private final EventsRepository eventsRepository;
    private final UserOperations userClient;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final RequestOperations requestClient;


    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя userId={}, from={}, size={}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Events> page = eventsRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(mapper::toShortDto)
                .map(dto -> {
                    UserShortDto initiator = getInitiator(userId);
                    return new EventShortDto(
                            dto.id(),
                            dto.title(),
                            dto.annotation(),
                            dto.category(),
                            initiator,
                            dto.paid(),
                            dto.eventDate(),
                            dto.views(),
                            dto.confirmedRequests()
                    );
                })
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание события: userId={}, payload={}", userId, newEventDto);
        UserDto user = userClient.findById(userId);
        log.info("В методе создания события получен пользователь с id {}", user.getId());

        LocalDateTime newDate = newEventDto.getEventDate();
        if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Нарушено ограничение по дате при добавлении нового события userId={}, newDate={}",
                    userId, newDate);
            throw new ValidationException(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила."
            );
        }

        Category category = categoryService.findCategoryEntityById(newEventDto.getCategory());

        Location location = locationService.saveLocation(newEventDto.getLocation());
        log.debug("Сохранена локация id={}, lat={}, lon={}",
                location.getId(), location.getLat(), location.getLon());

        Events events = mapper.toEntity(newEventDto);

        events.setInitiatorId(userId);
        events.setCategory(category);
        events.setLocation(location);
        events.setState(EventState.PENDING);
        events.setCreatedOn(LocalDateTime.now());
        log.info(
                "Готовим событие к сохранению: userId={}, title='{}', eventDate={}, categoryId={}, locationId={}, paid={}, participantLimit={}",
                events.getInitiatorId(),
                events.getTitle(),
                events.getEventDate(),
                events.getCategory() != null ? events.getCategory().getId() : null,
                events.getLocation() != null ? events.getLocation().getId() : null,
                events.getPaid(),
                events.getParticipantLimit()
        );
        Events saved = eventsRepository.save(events);
        log.info("Событие создано успешно: eventId={}, userId={}", saved.getId(), userId);
        EventFullDto dto = mapper.toFullDto(saved);
        dto.setInitiator(getInitiator(saved.getInitiatorId()));
        log.info("DTO созданного события: eventId={}, userId={}", dto.getId(), dto.getInitiator().id());
        return dto;
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Обновление события eventId={} пользователем userId={}, body={}", eventId, userId, updateRequest);
        UserDto user = userClient.findById(userId);

        Events event = checkEvent(eventId);
        checkInitiator(userId, eventId, event);

        if (updateRequest.getStateAction() != StateActionUserUpdateEvent.SEND_TO_REVIEW &&
                event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Не удается обновить опубликованное событие,уже PUBLISHED");
        }

        if (updateRequest.getEventDate() != null) {
            LocalDateTime newDate = updateRequest.getEventDate();
            if (newDate.isBefore(LocalDateTime.now().plusHours(2))) {
                log.warn("Нарушено ограничение по дате при обновлении события id={}, userId={}, newDate={}",
                        eventId, userId, newDate);
                throw new ValidationException(
                        "Field: eventDate. Error: должно содержать дату, которая еще не наступила."
                );
            }
        }

        mapper.updateEventFromUserRequest(updateRequest, event);

        if (updateRequest.getCategory() != null) {
            Category newCategory = categoryService.findCategoryEntityById(updateRequest.getCategory());
            event.setCategory(newCategory);
        }

        if (updateRequest.getLocation() != null) {
            Location newLocation = locationService.saveLocation(updateRequest.getLocation());
            log.debug("Обновлена локация для события eventId={}: locationId={}, lat={}, lon={}",
                    eventId, newLocation.getId(), newLocation.getLat(), newLocation.getLon());
            event.setLocation(newLocation);
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW -> {
                    event.setState(EventState.PENDING);
                    log.info("Событие id={} отправлено на модерацию пользователем id={}", eventId, userId);
                }
                case CANCEL_REVIEW -> {
                    event.setState(EventState.CANCELED);
                    log.info("Событие id={} отменено пользователем id={}", eventId, userId);
                }
            }
        }
        Events saved = eventsRepository.save(event);
        log.info("Событие id={} успешно обновлено пользователем id={}, новое состояние={}",
                saved.getId(), userId, saved.getState());
        EventFullDto dto = mapper.toFullDto(saved);
        dto.setInitiator(getInitiator(userId));
        return dto;
    }


    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Получение события eventId={} пользователем userId={}", eventId, userId);

        Events event = checkEvent(eventId);
        checkInitiator(userId, eventId, event);

        EventFullDto dto = mapper.toFullDto(event);
        dto.setInitiator(getInitiator(userId));
        return dto;
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId) {
        log.info("Получение запросов на участие в событии eventId={} пользователя userId={}", eventId, userId);
        //UserDto user = userClient.findById(userId);
        Events event = eventsRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Событие с Id " + eventId + " не найдено."));
        checkInitiator(userId, eventId, event);
        return requestClient.findByEventId(userId, eventId);
    }

    @Override
    public EventRequestStatusUpdateResult rejectingRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("Изменение статуса заявок на участие: userId={}, eventId={}, body={}",
                userId, eventId, updateRequest);

        userClient.findById(userId);
        Events event = eventsRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow( () -> new NotFoundException("Событие не найдено"));
        checkInitiator(userId, eventId, event);
        long confirmedRequests = requestClient.getConfirmedRequests(eventId);
        if(confirmedRequests == event.getParticipantLimit()
                && updateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            throw new ConflictException("Достигнут лимит заявок на событие");
        }
        return requestClient.updateRequestStatus(userId, eventId, updateRequest);
    }


    private Events checkEvent(Long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено", eventId);
                    return new NotFoundException("Event with id=" + eventId + " not found");
                });
    }

    private static void checkInitiator(Long userId, Long eventId, Events event) {
        if (!event.getInitiatorId().equals(userId)) {
            throw new ConflictException("User " + userId + " is not initiator of event " + eventId);
        }
    }

    private UserShortDto getInitiator(Long initiatorId){
        UserShortDto result = userClient.findShortDto(initiatorId);
        log.info("В методе получения пользователя при создании события получен пользователь с id {}", result.id());
        return result;
    }

//    private void validateRequestIdsNotEmpty(List<Long> requestIds, Long eventId) {
//        if (requestIds == null || requestIds.isEmpty()) {
//            log.warn("Пустой список requestIds при изменении статуса заявок для eventId={}", eventId);
//            throw new ConflictException("RequestIds must not be empty");
//        }
//    }


}
