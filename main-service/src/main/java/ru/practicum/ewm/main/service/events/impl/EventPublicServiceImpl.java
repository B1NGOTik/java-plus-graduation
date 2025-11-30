package ru.practicum.ewm.main.service.events.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.events.EventsMapper;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.params.PublicEventSearchParams;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.service.events.EventPublicService;
import ru.practicum.ewm.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventsRepository eventsRepository;
    private final EventsMapper eventsMapper;
    private final StatsClient statsClient;


    public List<EventShortDto> getEvents(PublicEventSearchParams params,
                                         HttpServletRequest request) {
        saveHit(request);
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());

        Page<Events> page = eventsRepository.findPublicEvents(params, pageable);


        return page.getContent().stream()
                .map(eventsMapper::toShortDto)
                .toList();
    }

    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        saveHit(request);
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        return eventsMapper.toFullDto(events);
    }


    //Отправка хита в сервис статистики.
    private void saveHit(HttpServletRequest request) {
        EndpointHitDto hit = new EndpointHitDto(
                null,
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(FORMATTER)
        );
        log.info("Отправляем хит в stats-сервис: {}", hit);
        statsClient.saveHit(hit);
    }
}
