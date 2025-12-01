package ru.practicum.ewm.main.mapper.events;

import org.mapstruct.*;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventsMapper {

    @Mapping(target = "category",
            expression = "java(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()))")
    @Mapping(target = "initiator",
            expression = "java(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()))")
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    Events toEntity(NewEventDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromUserRequest(
            UpdateEventUserRequest source,
            @MappingTarget Events target
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventFromAdminRequest(
            UpdateEventAdminRequest source,
            @MappingTarget Events target
    );

}
