package ru.practicum.ewm.main.service.events.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.events.EventsMapper;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.dto.NewEventDto;
import ru.practicum.ewm.main.model.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.repository.user.UserRepository;
import ru.practicum.ewm.main.service.events.EventAuthorizedService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class EventAuthorizedServiceImpl implements EventAuthorizedService {

    private final EventsMapper mapper;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        log.info("Получение событий пользователя userId={}, from={}, size={}", userId, from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Events> page = eventsRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(mapper::toShortDto)
                .toList();
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание события: userId={}, payload={}", userId, newEventDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден при создании события", userId);
                    return new NotFoundException("User with id=" + userId + " not found");
                });

        Events events = mapper.toEntity(newEventDto);

        events.setInitiator(user);
        events.setState(EventState.PENDING);
        events.setCreatedOn(LocalDateTime.now());
        log.info(
                "Готовим событие к сохранению: userId={}, title='{}', eventDate={}, categoryId={}, paid={}, participantLimit={}",
                userId,
                events.getTitle(),
                events.getEventDate(),
                events.getCategory() != null ? events.getCategory().getId() : null,
                events.getPaid(),
                events.getParticipantLimit()
        );
        Events saved = eventsRepository.save(events);
        log.info("Событие создано успешно: eventId={}, userId={}", saved.getId(), userId);
        return mapper.toFullDto(saved);
    }


    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Обновление события eventId={} пользователем userId={}, body={}", eventId, userId, updateRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден при создании события", userId);
                    return new NotFoundException("User with id=" + userId + " not found");
                });
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено при обновлении пользователем id={}", eventId, userId);
                    return new NotFoundException("Event with id=" + eventId + " not found");
                });

        if (!event.getInitiator().getId().equals(userId)) {
            throw new IllegalStateException("User " + userId + " is not initiator of event " + eventId);
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new IllegalStateException("Cannot update published event");
        }

        mapper.updateEventFromUserRequest(updateRequest, event);

        Events saved = eventsRepository.save(event);
        return mapper.toFullDto(saved);
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Получение события eventId={} пользователем userId={}", eventId, userId);

        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено", eventId);
                    return new NotFoundException("Event with id=" + eventId + " not found");
                });
        if (!event.getInitiator().getId().equals(userId)) {
            throw new IllegalStateException("User " + userId + " is not initiator of event " + eventId);
        }

        return mapper.toFullDto(event);
    }
}
