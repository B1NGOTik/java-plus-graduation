package ru.yandex.practicum.event.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.event.category.CategoryDto;
import ru.yandex.practicum.user.UserShortDto;

import java.time.LocalDateTime;

public record EventShortDto(
        Long id,
        String title,
        String annotation,
        CategoryDto category,
        UserShortDto initiator,
        Boolean paid,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        Double rating,
        Long confirmedRequests
) {
}
