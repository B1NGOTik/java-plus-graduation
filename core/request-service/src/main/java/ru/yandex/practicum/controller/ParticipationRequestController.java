package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.ParticipationRequestDto;
import ru.yandex.practicum.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class ParticipationRequestController {
    private final ParticipationRequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> add(@PathVariable Long userId,
                                                       @NotNull @RequestParam Long eventId) {
        log.info("Обращение к методу создания запроса в контроллере: userId {} eventId {}", userId, eventId);
        ParticipationRequestDto result = requestService.add(userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> findByRequesterId(@PathVariable Long userId) {
        List<ParticipationRequestDto> result = requestService.findByRequesterId(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<ParticipationRequestDto>> findByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        List<ParticipationRequestDto> result = requestService.findEventRequests(eventId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequestStatus(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @RequestBody EventRequestStatusUpdateRequest updateRequest){
        EventRequestStatusUpdateResult result = requestService.changeRequestStatus(userId, eventId, updateRequest);
        return ResponseEntity.ok(result);
    }
}
