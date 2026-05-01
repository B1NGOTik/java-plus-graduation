package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.yandex.practicum.repository.compilation.CompilationEventCompositeKey;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "compilation_event")
@IdClass(CompilationEventCompositeKey.class)
public class CompilationEvent {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Compilation compilation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Events event;
}
