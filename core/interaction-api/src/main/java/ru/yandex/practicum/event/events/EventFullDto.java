package ru.yandex.practicum.event.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.event.category.CategoryDto;
import ru.yandex.practicum.event.events.enums.EventState;
import ru.yandex.practicum.event.location.LocationDto;
import ru.yandex.practicum.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {

    // краткое описание
    String annotation;

    // категория
    CategoryDto category;

    // РАСЧЁТНОЕ поле (не хранится в Event)
    Long confirmedRequests;

    // дата создания
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    // полное описание
    String description;

    // дата проведения
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    // идентификатор
    Long id;

    // инициатор события
    UserShortDto initiator;

    // широта/долгота
    LocationDto location;

    // платное ли событие
    Boolean paid;

    // лимит участников (0 — без лимита)
    Integer participantLimit;

    // дата публикации
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    // нужна ли премодерация заявок
    Boolean requestModeration;

    // PENDING / PUBLISHED / CANCELED
    EventState state;

    // заголовок
    String title;

    // РАСЧЁТНОЕ поле (не хранится в Event)
    Double rating;
}
