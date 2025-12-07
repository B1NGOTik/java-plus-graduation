package ru.practicum.ewm.stats.server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.ViewStatsDto;

@UtilityClass
public class StatsDtoMapper {
    public ViewStatsDto toDto(Object[] objects) {
        return ViewStatsDto.builder()
                .app((String) objects[0])
                .uri((String) objects[1])
                .hits(((Long) objects[2]))
                .build();
    }
}
