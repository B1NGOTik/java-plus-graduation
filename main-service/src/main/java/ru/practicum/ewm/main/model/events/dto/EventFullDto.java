package ru.practicum.ewm.main.model.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.main.model.category.CategoryDto;
import ru.practicum.ewm.main.model.events.enums.EventState;
import ru.practicum.ewm.main.model.user.UserShortDto;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFullDto {

    // краткое описание
    private String annotation;

    // категория
    private CategoryDto category;

    // РАСЧЁТНОЕ поле (не хранится в Event)
    private Long confirmedRequests;

    // дата создания
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    // полное описание
    private String description;

    // дата проведения
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    // идентификатор
    private Long id;

    // инициатор события
    private UserShortDto initiator;

    // широта/долгота
    private LocationDto location;

    // платное ли событие
    private Boolean paid;

    // лимит участников (0 — без лимита)
    private Integer participantLimit;

    // дата публикации
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    // нужна ли премодерация заявок
    private Boolean requestModeration;

    // PENDING / PUBLISHED / CANCELED
    private EventState state;

    // заголовок
    private String title;

    // РАСЧЁТНОЕ поле (не хранится в Event)
    private Long views;
}