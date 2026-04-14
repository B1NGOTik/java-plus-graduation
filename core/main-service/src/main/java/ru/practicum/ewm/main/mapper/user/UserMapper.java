package ru.practicum.ewm.main.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.main.model.user.NewUserRequest;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.model.user.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(NewUserRequest userDto);
}
