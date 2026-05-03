package ru.yandex.practicum.service.events;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.event.events.params.PublicEventSearchParams;

import java.util.List;
import java.util.stream.Stream;

public interface EventPublicService {

    List<EventShortDto> getEvents(PublicEventSearchParams params,
                                  HttpServletRequest request);

    EventFullDto getById(Long eventId,
                         HttpServletRequest request, Long userId);

    EventFullDto getById(Long eventId);

    @Transactional
    void likeEvent(Long userId, Long eventId);

    Stream<RecommendedEventProto> getRecommendations(Long userId, int maxResults);
}
