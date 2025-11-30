package ru.practicum.ewm.main.model.events.params;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PublicEventSearchParams {
    private String text;
    private List<Long> categories;
    private Boolean paid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeStart;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private String sort;
    private Integer from = 0;
    private Integer size = 10;

    public Pageable toPageable() {
        int s = size != null && size > 0 ? size : 10;
        int f = from != null && from >= 0 ? from : 0;
        return PageRequest.of(f / s, s);
    }
}
