package ru.practicum.ewm.main.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.enums.ParticipationRequestStatus;
import ru.practicum.ewm.main.model.request.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventIdAndIdIn(Long eventId, List<Long> ids);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    long countByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

}
