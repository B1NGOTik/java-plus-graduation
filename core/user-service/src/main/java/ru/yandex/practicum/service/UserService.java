package ru.yandex.practicum.service;


import ru.yandex.practicum.model.User;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequest newUser);

    void deleteUser(Long userId);

    List<UserDto> findUsers(List<Long> ids, int from, int size);

    User findUserById(Long userId);
}
