package ru.practicum.ewm.main.model.compilation;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
