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
import ru.practicum.ewm.main.exception.ValidationException;
import ru.practicum.ewm.main.mapper.events.EventsMapper;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.events.params.PublicEventSearchParams;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.service.events.EventPublicService;
import ru.practicum.ewm.main.service.request.ParticipationRequestValidator;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventsRepository eventsRepository;
    private final EventsMapper eventsMapper;
    private final StatsClient statsClient;
    private final ParticipationRequestValidator requestValidator;


    public List<EventShortDto> getEvents(PublicEventSearchParams params,
                                         HttpServletRequest request) {
        log.info("Поиск публичных событий params={}", params);
        validateSearchParams(params);
        saveHit(request);

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Page<Events> page = eventsRepository.findPublicEvents(params, pageable);

        List<Events> events = page.getContent();
        if (events.isEmpty()) {
            return List.of();
        }

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();
        Map<String, Long> viewsByUri = getViewsForUris(uris);


        return events.stream()
                .map(e -> {
                    EventShortDto dto = eventsMapper.toShortDto(e);
                    String uri = "/events/" + e.getId();

                    long views = viewsByUri.getOrDefault(uri, 0L);
                    return new EventShortDto(
                            dto.id(),
                            dto.title(),
                            dto.annotation(),
                            dto.category(),
                            dto.initiator(),
                            dto.paid(),
                            dto.eventDate(),
                            views,
                            dto.confirmedRequests()
                    );
                })
                .toList();
    }


    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest request) {
        log.info("Публичный запрос события по id={}", eventId);
        saveHit(request);
        Events events = eventsRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        String uri = request.getRequestURI();
        long views = getViewsForUris(List.of(uri)).getOrDefault(uri, 0L);
        log.debug("Для события id={} по uri='{}' получено просмотров={}", eventId, uri, views);
        EventFullDto dto = eventsMapper.toFullDto(events);
        requestValidator.fillConfirmedRequests(dto);
        dto.setViews(views);

        log.info("Событие отдано клиенту: id={}, views={}", dto.getId(), dto.getViews());
        return dto;
    }

    private Map<String, Long> getViewsForUris(List<String> uris) {
        String start = "2000-01-01 00:00:00";
        String end = LocalDateTime.now().format(FORMATTER);

        log.info("Запрашиваем статистику: start={}, end={}, uris={}", start, end, uris);

        List<ViewStatsDto> stats = statsClient.getStats(start, end, uris, true);

        Map<String, Long> result = new HashMap<>();
        for (ViewStatsDto stat : stats) {
            result.put(stat.getUri(), stat.getHits());
        }
        return result;
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
        try {
            statsClient.saveHit(hit);
        } catch (Exception e) {
            log.error("Не удалось отправить хит в stats-сервис: {}", e.getMessage(), e);
        }
    }

    private void validateSearchParams(PublicEventSearchParams params) {
        LocalDateTime start = params.getRangeStart();
        LocalDateTime end = params.getRangeEnd();

        if (start != null && end != null && start.isAfter(end)) {
            log.warn("Некорректный диапазон дат при поиске событий: rangeStart={} > rangeEnd={}",
                    start, end);
            throw new ValidationException(
                    "rangeStart must not be after rangeEnd"
            );
        }
    }
}
