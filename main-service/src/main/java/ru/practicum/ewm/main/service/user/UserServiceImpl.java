package ru.practicum.ewm.main.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.mapper.user.UserMapper;
import ru.practicum.ewm.main.model.user.NewUserRequest;
import ru.practicum.ewm.main.model.user.User;
import ru.practicum.ewm.main.model.user.UserDto;
import ru.practicum.ewm.main.repository.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(NewUserRequest newUser) {
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
}
