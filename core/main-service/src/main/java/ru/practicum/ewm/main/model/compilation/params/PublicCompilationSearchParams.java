package ru.practicum.ewm.main.model.compilation.params;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicCompilationSearchParams {
    private Boolean pinned = false;

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;
}
