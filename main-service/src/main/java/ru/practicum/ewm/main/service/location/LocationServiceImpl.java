package ru.practicum.ewm.main.service.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.mapper.events.LocationMapper;
import ru.practicum.ewm.main.model.events.Location;
import ru.practicum.ewm.main.model.events.dto.LocationDto;
import ru.practicum.ewm.main.repository.locations.LocationRepository;

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
