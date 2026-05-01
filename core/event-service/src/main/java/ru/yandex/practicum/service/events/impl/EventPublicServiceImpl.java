package ru.yandex.practicum.service.events.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.event.events.enums.EventState;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.mapper.events.EventsMapper;
import ru.yandex.practicum.model.Events;
import ru.yandex.practicum.repository.events.EventsRepository;
import ru.yandex.practicum.request.RequestOperations;
import ru.yandex.practicum.service.events.EventPublicService;
import ru.yandex.practicum.user.UserOperations;
import ru.yandex.practicum.user.UserShortDto;

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
    private final UserOperations userClient;
    private final RequestOperations requestClient;
    //private final ParticipationRequestValidator requestValidator;


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
                    UserShortDto initiator = getInitiator(e.getInitiatorId());
                    String uri = "/events/" + e.getId();


                    long views = viewsByUri.getOrDefault(uri, 0L);
                    return new EventShortDto(
                            dto.id(),
                            dto.title(),
                            dto.annotation(),
                            dto.category(),
                            initiator,
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

        UserShortDto initiator = getInitiator(events.getInitiatorId());
        String uri = request.getRequestURI();
        long views = getViewsForUris(List.of(uri)).getOrDefault(uri, 0L);
        log.debug("Для события id={} по uri='{}' получено просмотров={}", eventId, uri, views);
        EventFullDto dto = eventsMapper.toFullDto(events);
        //requestValidator.fillConfirmedRequests(dto);
        dto.setViews(views);
        dto.setInitiator(initiator);
        log.info("Событие отдано клиенту: id={}, views={}", dto.getId(), dto.getViews());
        return dto;
    }

    @Override
    public EventFullDto getById(Long eventId) {
        log.info("Админский запрос события по id={}", eventId);
        Events events = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));
        UserShortDto initiator = getInitiator(events.getInitiatorId());
        EventFullDto dto = eventsMapper.toFullDto(events);
        dto.setInitiator(initiator);
        dto.setConfirmedRequests(requestClient.getConfirmedRequests(eventId));
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

    private UserShortDto getInitiator(Long initiatorId){
        UserShortDto result = userClient.findShortDto(initiatorId);
        log.info("В методе получения организатора события получен пользователь с id {}", result.id());
        return result;
    }
}
