package ru.practicum.ewm.main.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.mapper.user.UserMapper;
import ru.practicum.ewm.main.model.user.NewUserRequest;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.model.user.UserDto;
import ru.practicum.ewm.main.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(NewUserRequest newUser) {
        log.info("Создание пользователя: name={}, email={}",
                newUser.getName(), newUser.getEmail());
        if (userRepository.existsByEmail(newUser.getEmail())) {
            log.warn("Попытка создания пользователя с уже занятым email={}",
                    newUser.getEmail());
            throw new ConflictException(
                    String.format("User with email=%s already exists", newUser.getEmail())
            );
        }
        User user = userMapper.toModel(newUser);
        User save = userRepository.save(user);

        return userMapper.toDto(save);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with id=" + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);

        return (ids == null || ids.isEmpty()
                ? userRepository.findAll(page)
                : userRepository.findAllByIdIn(ids, page)
        )
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public User findUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь с id={} не найден при создании события", userId);
                    return new NotFoundException("User with id=" + userId + " not found");
                });

    }
}
