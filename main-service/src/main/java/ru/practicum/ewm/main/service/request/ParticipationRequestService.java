package ru.practicum.ewm.main.service.request;

import ru.practicum.ewm.main.model.request.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface ParticipationRequestService {
    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> findByRequesterId(Long requesterId);

    List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId);

    Integer findConfirmedRequestsCount(Long eventId);

    Map<Long, Long> countConfirmedRequestsByEventIds(List<Long> eventIds);
}
