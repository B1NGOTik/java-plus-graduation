package ru.yandex.practicum.mapper.compilation;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.event.compilation.CompilationDto;
import ru.yandex.practicum.event.compilation.NewCompilationDto;
import ru.yandex.practicum.event.events.EventShortDto;
import ru.yandex.practicum.model.Compilation;


import java.util.List;

@UtilityClass
public class CompilationMapper {
    public static Compilation toModel(NewCompilationDto dto) {
        return Compilation.builder()
                .pinned(dto.getPinned())
                .title(dto.getTitle())
                .build();
    }

    public static CompilationDto toDto(Compilation model, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(model.getId())
                .pinned(model.isPinned())
                .title(model.getTitle())
                .events(events)
                .build();
    }
}
