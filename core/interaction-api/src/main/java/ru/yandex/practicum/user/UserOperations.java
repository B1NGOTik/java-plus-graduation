package ru.yandex.practicum.user;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserOperations {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserRequest newUser);

    @GetMapping
    public List<UserDto> find(@RequestParam(required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") int from,
                              @RequestParam(defaultValue = "10") int size);


    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId);

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId);

    @GetMapping("/{userId}/short")
    public UserShortDto findShortDto(@PathVariable Long userId);
}
