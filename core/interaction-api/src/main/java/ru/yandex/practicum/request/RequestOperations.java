package ru.yandex.practicum.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "request-service")
public interface RequestOperations {

    @PostMapping("/users/{userId}/requests?eventId={eventId}")
    ParticipationRequestDto add(@PathVariable("userId") Long userId,
                                @RequestParam("eventId") Long eventId);

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
                                          @PathVariable("requestId") Long requestId);

    @GetMapping("/users/{userId}/requests")
    List<ParticipationRequestDto> findByRequesterId(@PathVariable("userId") Long userId);

    @GetMapping("/users/{userId}/requests/{eventId}")
    List<ParticipationRequestDto> findByEventId(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId);

    // ВАЖНО: URL должен совпадать с контроллером в request-service
    @RequestMapping(method = RequestMethod.POST, value = "/users/{userId}/requests/{eventId}")
    EventRequestStatusUpdateResult updateRequestStatus(@PathVariable("userId") Long userId,
                                                       @PathVariable("eventId") Long eventId,
                                                       @RequestBody EventRequestStatusUpdateRequest status);

    @GetMapping("/requests/{eventId}/confirmed")
    long getConfirmedRequests(@PathVariable("eventId") Long eventId);

    @GetMapping("/requests/ids")
    List<ParticipationRequestDto> findByIds(@RequestBody List<Long> ids);
}