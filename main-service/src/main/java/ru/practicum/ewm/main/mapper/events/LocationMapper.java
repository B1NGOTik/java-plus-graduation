package ru.practicum.ewm.main.mapper.events;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.main.model.events.Location;
import ru.practicum.ewm.main.model.events.dto.LocationDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {
    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationDto dto);

    LocationDto toDto(Location entity);
}
