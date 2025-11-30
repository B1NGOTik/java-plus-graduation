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
    private final EventsMapper eventsMapper;

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        Page<Events> page = eventsRepository.findAllByInitiatorId(userId, pageable);

        return page.getContent().stream()
                .map(mapper::toShortDto)
                .toList();

    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден при создании события", userId);
                    return new NotFoundException("User with id=" + userId + " not found");
                });

        Events events = eventsMapper.toEntity(newEventDto);

        events.setInitiator(user);
        events.setState(EventState.PENDING);
        events.setCreatedOn(LocalDateTime.now());
        Events saved = eventsRepository.save(events);
        log.info("Событие создано: id={}", saved.getId());
        return eventsMapper.toFullDto(saved);
    }


    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        return null;
    }
}
