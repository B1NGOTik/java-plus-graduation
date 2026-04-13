package ru.practicum.ewm.main.model.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {

    /**
     * Краткое описание (аннотация).
     * min 20, max 2000 символов
     */
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    /**
     * ID категории (а не сама категория!).
     * В спецификации поле называется category.
     */
    @NotNull
    private Long category;

    /**
     * Полное описание события.
     * min 20, max 7000, обязательно.
     */
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    /**
     * Дата и время события.
     * В JSON строка вида "yyyy-MM-dd HH:mm:ss"
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    /**
     * Место проведения: широта и долгота.
     */
    @NotNull
    private LocationDto location;

    /**
     * Платное ли событие. По умолчанию false.
     */
    @Builder.Default
    private Boolean paid = Boolean.FALSE;

    /**
     * Ограничение участников, 0 – без ограничения.
     */
    @PositiveOrZero
    @Builder.Default
    private Integer participantLimit = 0;

    /**
     * Нужна ли пре-модерация заявок.
     * По умолчанию true.
     */
    @Builder.Default
    private Boolean requestModeration = Boolean.TRUE;

    /**
     * Заголовок события.
     * min 3, max 120
     */
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}