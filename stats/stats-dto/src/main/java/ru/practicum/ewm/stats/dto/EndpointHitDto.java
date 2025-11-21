package ru.practicum.ewm.stats.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long id;

    String app;

    String uri;

    String ip;

    String timestamp;
}
