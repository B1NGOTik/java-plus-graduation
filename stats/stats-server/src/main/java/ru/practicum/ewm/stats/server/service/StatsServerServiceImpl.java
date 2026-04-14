package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.exception.ValidationException;
import ru.practicum.ewm.stats.server.mapper.HitDtoMapper;
import ru.practicum.ewm.stats.server.mapper.StatsDtoMapper;
import ru.practicum.ewm.stats.server.repository.StatsServerRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServerServiceImpl implements StatsServerService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsServerRepository statRepository;

    public EndpointHitDto saveHit(EndpointHitDto newHit) {
        return HitDtoMapper.toDto(statRepository.save(HitDtoMapper.toModel(newHit)));
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        List<Object[]> result;
        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            startTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            endTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage());
        }

        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Дата начала периода не может быть позже даты конца периода");
        }

        if (unique) {
            if (uris != null) {
                result = statRepository.findAllStatsBySelectedUrisAndUniqueIp(startTime, endTime, uris);
            } else {
                result = statRepository.findAllStatsByUniqueIp(startTime, endTime);
            }
        } else {
            if (uris != null) {
                result = statRepository.findAllStatsBySelectedUris(startTime, endTime, uris);
            } else {
                result = statRepository.findAllStats(startTime, endTime);
            }
        }

        return result.stream()
                .map(StatsDtoMapper::toDto)
                .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed())
                .toList();
    }
}
