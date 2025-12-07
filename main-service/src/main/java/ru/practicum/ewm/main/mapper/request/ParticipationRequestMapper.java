package ru.practicum.ewm.main.mapper.request;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.model.request.ParticipationRequest;
import ru.practicum.ewm.main.model.request.dto.ParticipationRequestDto;

@UtilityClass
public class ParticipationRequestMapper {
    public static ParticipationRequestDto toDto(ParticipationRequest model) {
        return ParticipationRequestDto.builder()
                .id(model.getId())
                .created(model.getCreated())
                .event(model.getEvent().getId())
                .requester(model.getRequester().getId())
                .status(model.getStatus().toString())
                .build();
    }
}
