package ru.practicum.ewm.main.service.request;

import ru.practicum.ewm.main.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.main.model.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.main.model.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> findByRequesterId(Long requesterId);

    List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);
}
