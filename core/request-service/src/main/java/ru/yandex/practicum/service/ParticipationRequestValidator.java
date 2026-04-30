package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventOperations;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.model.ParticipationRequest;
import ru.yandex.practicum.repository.ParticipationRequestRepository;
import ru.yandex.practicum.request.ParticipationRequestStatus;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParticipationRequestValidator {
    private final ParticipationRequestRepository requestRepository;
    private final EventOperations eventClient;

    public EventFullDto checkEventForInitiator(Long userId,
                                         Long eventId) {
        EventFullDto event = eventClient.getById(eventId);
        log.info("Получено событие с id {} и userId {}", event.getId(), event.getInitiator().id());

        if (!event.getInitiator().id().equals(userId)) {
            log.warn("Пользователь id={} не инициатор события id={}", userId, eventId);
            throw new ConflictException("Only event initiator can change request status");
        }

        return event;
    }

    public void checkRequestIdsNotEmpty(List<Long> requestIds, Long eventId) {
        if (requestIds == null || requestIds.isEmpty()) {
            log.warn("Пустой список requestIds при изменении статусов заявок для eventId={}", eventId);
            throw new ConflictException("RequestIds must not be empty");
        }
    }

    public List<ParticipationRequest> loadAndCheckRequests(ParticipationRequestRepository requestRepository,
                                                           Long eventId,
                                                           List<Long> requestIds) {
        List<ParticipationRequest> requests =
                requestRepository.findAllByEventIdAndIdIn(eventId, requestIds);

        if (requests.size() != requestIds.size()) {
            log.warn("Не все заявки найдены для eventId={}, requestIds={}", eventId, requestIds);
            throw new NotFoundException("Some requests were not found for this event");
        }

        for (ParticipationRequest r : requests) {
            if (r.getStatus() != ParticipationRequestStatus.PENDING) {
                log.warn("Попытка изменить статус заявки id={} со статусом {} (ожидался PENDING)",
                        r.getId(), r.getStatus());
                throw new ConflictException("Request must have status PENDING");
            }
        }
        return requests;
    }

    public int resolveParticipantLimit(EventFullDto event) {
        Integer limitObj = event.getParticipantLimit();
        return limitObj != null ? limitObj : 0;
    }


    public void checkLimitNotReached(int participantLimit,
                                     long confirmedCount,
                                     Long eventId) {
        if (participantLimit > 0 && confirmedCount >= participantLimit) {
            log.warn("Лимит участников уже достигнут: eventId={}, limit={}, confirmed={}",
                    eventId, participantLimit, confirmedCount);
            throw new ConflictException("The participant limit has been reached");
        }
    }

    /**
     * Обогащает одно событие числом подтверждённых заявок.
     */
    public void fillConfirmedRequests(EventFullDto dto) {
        if (dto == null || dto.getId() == null) {
            log.warn("fillConfirmedRequests: dto или dto.id == null, пропускаем");
            return;
        }

        long confirmed = requestRepository.countByEventIdAndStatus(
                dto.getId(),
                ParticipationRequestStatus.CONFIRMED
        );

        dto.setConfirmedRequests(confirmed);

        log.debug("Для события id={} установлено confirmedRequests={}",
                dto.getId(), confirmed);
    }

    public void fillConfirmedRequests(List<EventFullDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return;
        }
        for (EventFullDto dto : dtos) {
            fillConfirmedRequests(dto); // переиспользуем метод выше
        }
    }
}
