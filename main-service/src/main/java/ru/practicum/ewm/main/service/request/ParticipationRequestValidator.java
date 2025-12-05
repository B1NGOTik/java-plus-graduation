package ru.practicum.ewm.main.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.enums.ParticipationRequestStatus;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.request.ParticipationRequest;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.repository.request.ParticipationRequestRepository;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParticipationRequestValidator {


    private final ParticipationRequestRepository requestRepository;

    public Events checkEventForInitiator(EventsRepository eventsRepository,
                                         Long userId,
                                         Long eventId) {
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Событие с id={} не найдено при работе с заявками пользователем id={}",
                            eventId, userId);
                    return new NotFoundException("Event with id=" + eventId + " not found");
                });

        if (!event.getInitiator().getId().equals(userId)) {
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

    public int resolveParticipantLimit(Events event) {
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
