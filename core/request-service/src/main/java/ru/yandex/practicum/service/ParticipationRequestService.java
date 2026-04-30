package ru.yandex.practicum.service;

import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> findByRequesterId(Long requesterId);

    List<ParticipationRequestDto> findEventRequests(Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}
