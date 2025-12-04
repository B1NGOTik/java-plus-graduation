package ru.practicum.ewm.main.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.request.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    @Query("SELECT COUNT(pr) " +
            "FROM ParticipationRequest pr " +
            "WHERE pr.event.id = :eventId AND pr.status = 'CONFIRMED'")
    Integer findConfirmedRequestsCount(Long eventId);

    @Query("SELECT pr.event.id, COUNT(pr) " +
            "FROM ParticipationRequest pr " +
            "WHERE pr.event.id IN :ids AND pr.status = 'CONFIRMED'" +
            "GROUP BY pr.event.id")
    List<Object[]> countRequestsByEventIds(@Param("ids") List<Long> ids);
}
