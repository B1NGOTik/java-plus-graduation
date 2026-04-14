package ru.practicum.ewm.main.service.events.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.ValidationException;
import ru.practicum.ewm.main.mapper.events.EventsMapper;
import ru.practicum.ewm.main.model.category.Category;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.Location;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.dto.NewEventDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.events.enums.StateActionUserUpdateEvent;
import ru.practicum.ewm.main.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.model.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.service.category.CategoryService;
import ru.practicum.ewm.main.service.events.EventAuthorizedService;
import ru.practicum.ewm.main.service.location.LocationService;
import ru.practicum.ewm.main.service.request.ParticipationRequestService;
import ru.practicum.ewm.main.service.request.ParticipationRequestValidator;
import ru.practicum.ewm.main.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class EventAuthorizedServiceImpl implements EventAuthorizedService {

    private final EventsMapper mapper;
    private final EventsRepository eventsRepository;
    private final UserService userService;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final ParticipationRequestService requestService;
    private final ParticipationRequestValidator participationRequestValidator;


    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя userId={}, from={}, size={}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Events> page = eventsRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(mapper::toShortDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание события: userId={}, payload={}", userId, newEventDto);
        User user = userService.findUserById(userId);

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

        events.setInitiator(user);
        events.setCategory(category);
        events.setLocation(location);
        events.setState(EventState.PENDING);
        events.setCreatedOn(LocalDateTime.now());
        log.info(
                "Готовим событие к сохранению: userId={}, title='{}', eventDate={}, categoryId={}, locationId={}, paid={}, participantLimit={}",
                userId,
                events.getTitle(),
                events.getEventDate(),
                events.getCategory() != null ? events.getCategory().getId() : null,
                events.getLocation() != null ? events.getLocation().getId() : null,
                events.getPaid(),
                events.getParticipantLimit()
        );
        Events saved = eventsRepository.save(events);
        log.info("Событие создано успешно: eventId={}, userId={}", saved.getId(), userId);
        return mapper.toFullDto(saved);
    }

    @Transactional
    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Обновление события eventId={} пользователем userId={}, body={}", eventId, userId, updateRequest);
        User user = userService.findUserById(userId);

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
        return mapper.toFullDto(saved);
    }


    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Получение события eventId={} пользователем userId={}", eventId, userId);

        Events event = checkEvent(eventId);
        checkInitiator(userId, eventId, event);

        return mapper.toFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId) {
        log.info("Получение запросов на участие в событии eventId={} пользователя userId={}", eventId, userId);
        User user = userService.findUserById(userId);
        participationRequestValidator.checkEventForInitiator(
                eventsRepository,
                userId,
                eventId
        );
        return requestService.findEventRequests(userId, eventId);
    }

    @Override
    public EventRequestStatusUpdateResult rejectingRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("Изменение статуса заявок на участие: userId={}, eventId={}, body={}",
                userId, eventId, updateRequest);

        userService.findUserById(userId);
        participationRequestValidator.checkEventForInitiator(
                eventsRepository,
                userId,
                eventId
        );

        return requestService.changeRequestStatus(userId, eventId, updateRequest);
    }


    private Events checkEvent(Long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено", eventId);
                    return new NotFoundException("Event with id=" + eventId + " not found");
                });
    }

    private static void checkInitiator(Long userId, Long eventId, Events event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("User " + userId + " is not initiator of event " + eventId);
        }
    }

//    private void validateRequestIdsNotEmpty(List<Long> requestIds, Long eventId) {
//        if (requestIds == null || requestIds.isEmpty()) {
//            log.warn("Пустой список requestIds при изменении статуса заявок для eventId={}", eventId);
//            throw new ConflictException("RequestIds must not be empty");
//        }
//    }


}
