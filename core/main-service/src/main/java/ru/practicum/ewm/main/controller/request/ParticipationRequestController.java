package ru.practicum.ewm.main.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.main.service.request.ParticipationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class ParticipationRequestController {
    private final ParticipationRequestService requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> add(@PathVariable Long userId,
                                                       @NotNull @RequestParam Long eventId) {
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
}
