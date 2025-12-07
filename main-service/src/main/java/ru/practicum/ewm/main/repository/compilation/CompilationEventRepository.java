package ru.practicum.ewm.main.repository.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.compilation.CompilationEvent;

import java.util.List;
import java.util.Optional;

public interface CompilationEventRepository extends JpaRepository<CompilationEvent, CompilationEventCompositeKey> {
    @Query("""
            SELECT ce
            FROM CompilationEvent ce
            WHERE ce.compilation.id = :compilationId AND ce.event.id = :eventId
            """)
    Optional<CompilationEvent> findByCompilationIdAndEventId(@Param("compilationId") Long compilationId,
                                                             @Param("eventId") Long eventId);

    @Query("""
            SELECT ce
            FROM CompilationEvent ce
            JOIN FETCH ce.event e
            JOIN FETCH ce.compilation c
            WHERE ce.compilation.id IN :compilationIds
            """)
    List<CompilationEvent> findByCompilationIds(@Param("compilationIds") List<Long> compilationIds);

    @Query("""
            SELECT ce.event.id
            FROM CompilationEvent ce
            WHERE ce.compilation.id = :compilationId
            """)
    List<Long> findEventIdsByCompilationId(@Param("compilationId") Long compilationId);
}
