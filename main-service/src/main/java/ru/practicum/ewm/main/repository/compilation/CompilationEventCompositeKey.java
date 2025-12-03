package ru.practicum.ewm.main.repository.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEventCompositeKey implements Serializable {
    private Long compilation;
    private Long event;
}
