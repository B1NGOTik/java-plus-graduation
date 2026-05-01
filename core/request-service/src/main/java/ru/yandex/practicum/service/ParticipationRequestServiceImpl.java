package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.events.EventFullDto;
import ru.yandex.practicum.event.events.EventOperations;
import ru.yandex.practicum.exception.ConflictException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.mapper.ParticipationRequestMapper;
import ru.yandex.practicum.model.ParticipationRequest;
import ru.yandex.practicum.repository.ParticipationRequestRepository;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.request.ParticipationRequestStatus;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserOperations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserOperations userClient;
    private final EventOperations eventClient;
    private final ParticipationRequestValidator participationRequestValidator;

    @Override
    public ParticipationRequestDto add(Long requesterId, Long eventId) {
        UserDto requester = userClient.findById(requesterId);
        //log.info("Получили пользователя {} в сервисе", requesterId);
        EventFullDto event = eventClient.getById(eventId);
        //log.info("Получили событие {} в сервисе", eventId);

        if (requester == null) {
            log.warn("Не удалось получить пользователя с id: {}", requesterId);
            throw new NotFoundException("не найден пользователь с id " + requesterId);
        }

        Optional<ParticipationRequest> existing = requestRepository.findByRequesterIdAndEventId(requesterId, eventId);
        if (existing.isPresent()) {
            log.warn("Попытка добавить повторный запрос: requesterId={}, eventId={}", requesterId, eventId);
            throw new ConflictException("нельзя добавить повторный запрос");
        }

        if (requesterId.equals(event.getInitiator().id())) {
            log.warn("Инициатор пытается подать запрос на своё событие: userId={}, eventId={}",
                    requesterId, eventId);
            throw new ConflictException("инициатор события не может добавить запрос на участие в своём событии");
        }

        if (event.getPublishedOn() == null) {
            log.warn("Попытка подать заявку на неопубликованное событие: eventId={}, requesterId={}",
                    eventId, requesterId);
            throw new ConflictException("нельзя участвовать в неопубликованном событии");
        }

        long limit = event.getParticipantLimit() == null ? 0L : event.getParticipantLimit();
        if (limit != 0L) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(
                    eventId,
                    ParticipationRequestStatus.CONFIRMED
            );
            if (confirmedCount >= limit) {
                log.warn("Достигнут лимит участников: eventId={}, limit={}, confirmed={}",
                        eventId, limit, confirmedCount);
                throw new ConflictException("достигнут лимит запросов на участие");
            }
        }

        ParticipationRequest newRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .eventId(event.getId())
                .requesterId(requester.getId())
                .status(ParticipationRequestStatus.PENDING)
                .build();

        if (Boolean.FALSE.equals(event.getRequestModeration()) || limit == 0) {
            newRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
            log.debug("Премодерация отключена либо нет ограничений на количество участников, заявка будет сразу CONFIRMED: eventId={}, requesterId={}",
                    eventId, requesterId);
        }

        ParticipationRequest saved = requestRepository.save(newRequest);
        log.info("Запрос на участие создан: requestId={}, requesterId={}, eventId={}",
                saved.getId(), requesterId, eventId);

        return ParticipationRequestMapper.toDto(saved);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long requesterId, Long requestId) {
        UserDto requester = userClient.findById(requesterId);

        if (requester == null) {
            throw new NotFoundException("не найден пользователь с id " + requesterId);
        }

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("не найден запрос на событие с id " + requestId));
        request.setStatus(ParticipationRequestStatus.CANCELED);
        ParticipationRequest result = requestRepository.save(request);
        return ParticipationRequestMapper.toDto(result);
    }

    @Override
    public List<ParticipationRequestDto> findByRequesterId(Long requesterId) {
        UserDto requester = userClient.findById(requesterId);

        if (requester == null) {
            throw new NotFoundException("не найден пользователь с id " + requesterId);
        }
        List<ParticipationRequest> result = requestRepository.findByRequesterId(requesterId);

        return result.stream().map(ParticipationRequestMapper::toDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long eventId) {
        List<ParticipationRequest> result = requestRepository.findByEventId(eventId);

        return result.stream().map(ParticipationRequestMapper::toDto).toList();
    }


    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        log.info("Изменение статуса заявок: userId={}, eventId={}, body={}",
                userId, eventId, updateRequest);

        userClient.findById(userId);

        EventFullDto event = participationRequestValidator.checkEventForInitiator(userId, eventId);

        List<Long> requestIds = updateRequest.getRequestIds();
        if (event.getConfirmedRequests().intValue() == event.getParticipantLimit()
                && updateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            throw new ConflictException("Достигнут лимит заявок на это событие");
        }
        if (requestIds == null || requestIds.isEmpty()) {
            log.warn("Пустой список requestIds при изменении статуса заявок для eventId={}", eventId);
            throw new ConflictException("RequestIds must not be empty");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(requestIds);
        if (requests.size() != requestIds.size()) {
            log.warn("Не все заявки найдены: eventId={}, ожидалось {}, найдено {}",
                    eventId, requestIds.size(), requests.size());
            throw new NotFoundException("Some participation requests were not found");
        }

        for (ParticipationRequest r : requests) {
            if (!r.getEventId().equals(eventId)) {
                log.warn("Заявка id={} не принадлежит событию id={}", r.getId(), eventId);
                throw new ConflictException("Request does not belong to this event");
            }
        }

        for (ParticipationRequest r : requests) {
            if (r.getStatus() != ParticipationRequestStatus.PENDING) {
                log.warn("Попытка изменить статус заявки id={} со статусом {} (ожидался PENDING)",
                        r.getId(), r.getStatus());
                throw new ConflictException("Request must have status PENDING");
            }
        }

        ParticipationRequestStatus action = updateRequest.getStatus();
        if (action == null) {
            log.warn("Статус в EventRequestStatusUpdateRequest == null");
            throw new ConflictException("Status must not be null");
        }

        List<ParticipationRequestDto> confirmedDtos = new java.util.ArrayList<>();
        List<ParticipationRequestDto> rejectedDtos = new java.util.ArrayList<>();


        if (action == ParticipationRequestStatus.REJECTED) {
            for (ParticipationRequest r : requests) {
                r.setStatus(ParticipationRequestStatus.REJECTED);
                rejectedDtos.add(ParticipationRequestMapper.toDto(r));
            }
            requestRepository.saveAll(requests);

            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(confirmedDtos);
            result.setRejectedRequests(rejectedDtos);

            log.info("Отклонены заявки {} для eventId={} пользователем id={}",
                    requests.stream().map(ParticipationRequest::getId).toList(),
                    eventId, userId);

            return result;
        }

        if (action == ParticipationRequestStatus.CONFIRMED) {
            long limit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0L;

            long confirmedCount = requestRepository.countByEventIdAndStatus(
                    eventId,
                    ParticipationRequestStatus.CONFIRMED
            );

            if (limit != 0 && confirmedCount >= limit) {
                log.warn("Лимит участников уже достигнут для eventId={}", eventId);
                throw new ConflictException("The participant limit has been reached");
            }

            for (ParticipationRequest r : requests) {
                if (limit == 0) {
                    r.setStatus(ParticipationRequestStatus.CONFIRMED);
                    confirmedDtos.add(ParticipationRequestMapper.toDto(r));
                } else {
                    // есть лимит
                    if (confirmedCount < limit) {
                        r.setStatus(ParticipationRequestStatus.CONFIRMED);
                        confirmedCount++;
                        confirmedDtos.add(ParticipationRequestMapper.toDto(r));
                    } else {
                        r.setStatus(ParticipationRequestStatus.REJECTED);
                        rejectedDtos.add(ParticipationRequestMapper.toDto(r));
                    }
                }
            }

            requestRepository.saveAll(requests);

            if (limit != 0 && confirmedCount >= limit) {
                List<ParticipationRequest> pending = requestRepository
                        .findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING);

                for (ParticipationRequest p : pending) {
                    p.setStatus(ParticipationRequestStatus.REJECTED);
                    rejectedDtos.add(ParticipationRequestMapper.toDto(p));
                }

                if (!pending.isEmpty()) {
                    requestRepository.saveAll(pending);
                }

                log.info("После подтверждения достигнут лимит для eventId={}. " +
                                "Все оставшиеся PENDING-заявки отклонены, ids={}",
                        eventId,
                        pending.stream().map(ParticipationRequest::getId).toList());
            }

            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(confirmedDtos);
            result.setRejectedRequests(rejectedDtos);

            log.info("Изменены статусы заявок для eventId={}: confirmed={}, rejected={}",
                    eventId,
                    confirmedDtos.stream().map(ParticipationRequestDto::getId).toList(),
                    rejectedDtos.stream().map(ParticipationRequestDto::getId).toList());

            return result;
        }

        log.warn("Некорректный статус={} в EventRequestStatusUpdateRequest", action);
        throw new ConflictException("Unknown status: " + action);
    }

    @Override
    public long countConfirmedRequests(Long eventId) {
         long result = requestRepository.countByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
         log.info("В методе countByEventIdAndStatus возвращено значение {}", result);
         return result;
    }

    @Override
    public List<ParticipationRequestDto> findAllByIds(List<Long> ids) {
        return requestRepository.findAllById(ids).stream()
                .map(ParticipationRequestMapper::toDto)
                .toList();
    }
}
