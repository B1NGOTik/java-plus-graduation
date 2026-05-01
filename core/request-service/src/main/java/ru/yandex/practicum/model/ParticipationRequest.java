package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.request.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "ewm_requests")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "created")
    LocalDateTime created;
    @Column(name = "event_id")
    Long eventId;
    @Column(name = "requester_id")
    Long requesterId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ParticipationRequestStatus status;
}
