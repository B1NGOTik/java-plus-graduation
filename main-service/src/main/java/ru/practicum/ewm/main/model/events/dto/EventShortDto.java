package ru.practicum.ewm.main.model.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.main.model.category.CategoryDto;

import java.time.LocalDateTime;

public record EventShortDto(
        Long id,
        String title,
        String annotation,
        CategoryDto category,
        String categoryName,
        Boolean paid,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        Long views,
        Long confirmedRequests
) {
}