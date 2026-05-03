package ru.yandex.practicum.service.events.impl;

import com.google.protobuf.Timestamp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.AnalyzerClient;
import ru.practicum.CollectorClient;

import ru.practicum.ewm.stats.proto.*;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.event.events.enums.EventState;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.mapper.events.EventsMapper;
import ru.yandex.practicum.model.Events;
import ru.yandex.practicum.repository.events.EventsRepository;
import ru.yandex.practicum.request.RequestOperations;
import ru.yandex.practicum.service.events.EventPublicService;
import ru.yandex.practicum.user.UserOperations;
import ru.yandex.practicum.user.UserShortDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicServiceImpl implements EventPublicService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventsRepository eventsRepository;
    private final EventsMapper eventsMapper;

    private final UserOperations userClient;
    private final RequestOperations requestClient;
    private final CollectorClient collectorClient;
    private final AnalyzerClient analyzerClient;


    public List<EventShortDto> getEvents(PublicEventSearchParams params,
                                         HttpServletRequest request) {
        log.info("Поиск публичных событий params={}", params);
        validateSearchParams(params);

        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Page<Events> page = eventsRepository.findPublicEvents(params, pageable);

        List<Events> events = page.getContent();
        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = new ArrayList<>();
        for(Events event : events) {
            eventIds.add(event.getId());
        }

        Map<Long, Double> ratings = getRatingsForEvents(eventIds);

        return events.stream()
                .map(e -> {
                    EventShortDto dto = eventsMapper.toShortDto(e);
                    UserShortDto initiator = getInitiator(e.getInitiatorId());
                    String uri = "/events/" + e.getId();
                    Double rating = ratings.get(dto.id());

                    return new EventShortDto(
                            dto.id(),
                            dto.title(),
                            dto.annotation(),
                            dto.category(),
                            initiator,
                            dto.paid(),
                            dto.eventDate(),
                            rating,
                            dto.confirmedRequests()
                    );
                })
                .toList();
    }


    @Override
    public EventFullDto getById(Long eventId, HttpServletRequest request, Long userId) {
        log.info("Публичный запрос события по id={}", eventId);
        Events events = eventsRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.VIEW)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        collectorClient.sendUserAction(action);
        UserShortDto initiator = getInitiator(events.getInitiatorId());
        EventFullDto dto = eventsMapper.toFullDto(events);
        dto.setInitiator(initiator);
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

    private Map<Long, Double> getRatingsForEvents(List<Long> eventIds) {
        if (eventIds.isEmpty()) return Map.of();

        Map<Long, Double> ratings = eventIds.stream()
                .collect(Collectors.toMap(id -> id, id -> 0.0));

        try {
            InteractionsCountRequestProto request = InteractionsCountRequestProto.newBuilder()
                    .addAllEventId(eventIds)
                    .build();

            analyzerClient.getInteractionsCount(request)
                    .forEach(proto -> ratings.put(proto.getEventId(), proto.getScore()));
        } catch (Exception e) {
            log.warn("Не удалось получить рейтинги для событий", e);
        }

        return ratings;
    }

    @Transactional
    @Override
    public void likeEvent(Long userId, Long eventId) {
        log.info("Лайк события eventId={} от пользователя userId={}", eventId, userId);

        EventFullDto event = getById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new ValidationException("Нельзя лайкнуть неопубликованное событие");
        }


        UserActionProto action = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(ActionTypeProto.LIKE)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        collectorClient.sendUserAction(action);
    }

    @Override
    public Stream<RecommendedEventProto> getRecommendations(Long userId, int maxResults) {
        log.info("Получение рекомендаций для пользователя userId={}, maxResults={}", userId, maxResults);

        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();

        return analyzerClient.getRecommendationsForUser(request);
    }
}
