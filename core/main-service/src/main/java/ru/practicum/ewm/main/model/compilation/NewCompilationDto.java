package ru.practicum.ewm.main.model.compilation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NewCompilationDto {

    private Set<Long> events;

    @Builder.Default
    private Boolean pinned = Boolean.FALSE;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @JsonCreator
    public static NewCompilationDto create(
            @JsonProperty("events") Set<Long> events,
            @JsonProperty("pinned") Boolean pinned,
            @JsonProperty("title") String title) {
        return NewCompilationDto.builder()
                .events(events != null ? events : Set.of())
                .pinned(pinned != null ? pinned : false)
                .title(title)
                .build();
    }
}
