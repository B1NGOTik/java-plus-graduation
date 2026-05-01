package ru.yandex.practicum.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    List<Long> requestIds;

    ParticipationRequestStatus status;
}
