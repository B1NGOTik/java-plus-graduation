package ru.practicum.ewm.main.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.enums.ParticipationRequestStatus;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.request.ParticipationRequestMapper;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.request.ParticipationRequest;
import ru.practicum.ewm.main.model.request.ParticipationRequestDto;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.repository.events.EventsRepository;
import ru.practicum.ewm.main.repository.request.ParticipationRequestRepository;
import ru.practicum.ewm.main.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserService userService;
    private final EventsRepository eventsRepository;

    @Override
    public ParticipationRequestDto add(Long requesterId, Long eventId) {
        User requester = userService.findUserById(requesterId);
        Events event = eventsRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("не найдено событие с id " + eventId));

        if (requester == null) {
            throw new NotFoundException("не найден пользователь с id " + requesterId);
        }

        Optional<ParticipationRequest> requestOpt = requestRepository.findByRequesterIdAndEventId(requesterId, eventId);
        if (requestOpt.isPresent()) {
            throw new ConflictException("нельзя добавить повторный запрос");
        }

        if (requesterId.equals(event.getInitiator().getId())) {
            throw new ConflictException("инициатор события не может добавить запрос на участие в своём событии");
        }

        if (event.getPublishedOn() == null) {
            throw new ConflictException("нельзя участвовать в неопубликованном событии");
        }

        if (!event.getParticipantLimit().equals(0)) {
            Integer confirmedRequestsCount = requestRepository.findConfirmedRequestsCount(eventId);
            if (confirmedRequestsCount >= event.getParticipantLimit()) {
                throw new ConflictException("достигнут лимит запросов на участие");
            }
        }

        ParticipationRequest newRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(ParticipationRequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            newRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        }

        ParticipationRequest result = requestRepository.save(newRequest);
        return ParticipationRequestMapper.toDto(result);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long requesterId, Long requestId) {
        User requester = userService.findUserById(requesterId);

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
        User requester = userService.findUserById(requesterId);

        if (requester == null) {
            throw new NotFoundException("не найден пользователь с id " + requesterId);
        }
        List<ParticipationRequest> result = requestRepository.findByRequesterId(requesterId);

        return result.stream().map(ParticipationRequestMapper::toDto).toList();
    }

    @Override
    public List<ParticipationRequestDto> findEventRequests(Long userId, Long eventId) {
        List<ParticipationRequest> result = requestRepository.findByEventId(eventId);

        return result.stream().map(ParticipationRequestMapper::toDto).toList();
    }

    @Override
    public Integer findConfirmedRequestsCount(Long eventId) {
        Events event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("не найдено событие с id " + eventId));

        return requestRepository.findConfirmedRequestsCount(eventId);
    }

    @Override
    public Map<Long, Long> countConfirmedRequestsByEventIds(List<Long> eventIds) {
        List<Object[]> counts = requestRepository.countRequestsByEventIds(eventIds);
        Map<Long, Long> result = new HashMap<>();
        for (Object[] o: counts) {
            result.put((Long)o[0], (Long)o[1]);
        }
        return result;
    }
}
