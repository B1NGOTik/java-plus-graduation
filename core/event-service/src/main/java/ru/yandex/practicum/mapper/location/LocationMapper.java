package ru.yandex.practicum.mapper.location;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.event.location.LocationDto;
import ru.yandex.practicum.model.Location;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {
    @Mapping(target = "id", ignore = true)
    Location toEntity(LocationDto dto);

    LocationDto toDto(Location entity);
}
