package ru.practicum.ewm.main.model.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.enums.ParticipationRequestStatus;

import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    List<Long> eventIds;

    ParticipationRequestStatus status;
}
