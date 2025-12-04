package ru.practicum.ewm.stats.server.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.service.StatsServerService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsServerController {
    private final StatsServerService statService;

    @PostMapping("/hit")
    public ResponseEntity<String> saveHit(@RequestBody EndpointHitDto hitDto) {
        EndpointHitDto savedHit = statService.saveHit(hitDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена");
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStats(@RequestParam @NotNull String start,
                                                @RequestParam @NotNull String end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        List<ViewStatsDto> result = statService.getStats(start, end, uris, unique);

        return ResponseEntity.ok(result);
    }
}
