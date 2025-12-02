package ru.practicum.ewm.main.model.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.main.model.category.CategoryDto;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.user.UserShortDto;

import java.time.LocalDateTime;


public record EventFullDto(

        String annotation,              // краткое описание

        CategoryDto category,           // категория

        Long confirmedRequests,         // РАСЧЁТНОЕ поле (не хранится в Event)

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,        // дата создания

        String description,             // полное описание

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,        // дата проведения

        Long id,                        // идентификатор

        UserShortDto initiator,         // инициатор события

        LocationDto location,           // широта/долгота

        Boolean paid,                   // платное ли событие

        Integer participantLimit,       // лимит участников (0 — без лимита)

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,      // дата публикации

        Boolean requestModeration,      // нужна ли премодерация заявок

        EventState state,               // PENDING / PUBLISHED / CANCELED

        String title,                   // заголовок

        Long views                      // РАСЧЁТНОЕ поле (не хранится в Event)
) {
}
