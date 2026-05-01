package ru.yandex.practicum.event.compilation;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.event.events.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
