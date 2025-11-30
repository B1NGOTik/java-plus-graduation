package ru.practicum.ewm.main.mapper.events;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventFullDto;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.model.events.dto.NewEventDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventsMapper {
    @Mapping(target = "category",
            expression = "java(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    EventShortDto toShortDto(Events event);


    @Mapping(target = "category",
            expression = "java(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))")
    @Mapping(target = "initiator",
            expression = "java(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))")
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    EventFullDto toFullDto(Events event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    Events toEntity(NewEventDto dto);
}
