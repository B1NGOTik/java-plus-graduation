package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.UserService;
import ru.yandex.practicum.user.NewUserRequest;
import ru.yandex.practicum.user.UserDto;
import ru.yandex.practicum.user.UserOperations;
import ru.yandex.practicum.user.UserShortDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin/users")
public class UserController implements UserOperations {
    private final UserService userService;

    @Override
    public UserDto create(NewUserRequest newUser) {
        return userService.createUser(newUser);
    }

    @Override
    public List<UserDto> find(List<Long> ids, int from, int size) {
        return userService.findUsers(ids, from, size);
    }

    @Override
    public UserDto findById(Long userId) {
        return userService.findUserDtoById(userId);
    }

    @Override
    public void delete(Long userId) {
        userService.deleteUser(userId);
    }

    @Override
    public UserShortDto findShortDto(Long userId) {
        return userService.findUserShortDtoById(userId);
    }
}
