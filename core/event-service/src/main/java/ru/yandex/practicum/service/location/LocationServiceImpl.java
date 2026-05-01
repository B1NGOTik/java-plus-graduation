package ru.yandex.practicum.service.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.event.location.LocationDto;
import ru.yandex.practicum.mapper.location.LocationMapper;
import ru.yandex.practicum.model.Location;
import ru.yandex.practicum.repository.LocationRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public Location saveLocation(LocationDto dto) {
        log.info("Создание локации lat={}, lon={}", dto.lat(), dto.lon());
        Location location = locationMapper.toEntity(dto);
        Location saved = locationRepository.save(location);
        log.debug("Локация сохранена: id={}, lat={}, lon={}",
                saved.getId(), saved.getLat(), saved.getLon());
        return saved;
    }
}
