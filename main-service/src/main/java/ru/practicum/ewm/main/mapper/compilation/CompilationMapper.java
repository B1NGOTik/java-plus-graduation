package ru.practicum.ewm.main.mapper.compilation;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.model.compilation.Compilation;
import ru.practicum.ewm.main.model.compilation.CompilationDto;
import ru.practicum.ewm.main.model.compilation.NewCompilationDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;

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
