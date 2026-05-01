package ru.yandex.practicum.event.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.event.events.enums.StateActionUserUpdateEvent;
import ru.yandex.practicum.event.location.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    /**
     * Действие пользователя со статусом события:
     * отправить на модерацию или отменить.
     */
    private StateActionUserUpdateEvent stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
