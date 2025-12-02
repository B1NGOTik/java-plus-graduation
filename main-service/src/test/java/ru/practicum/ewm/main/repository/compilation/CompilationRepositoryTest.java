package ru.practicum.ewm.main.repository.compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.ewm.main.model.category.Category;
import ru.practicum.ewm.main.model.compilation.Compilation;
import ru.practicum.ewm.main.model.compilation.CompilationEvent;
import ru.practicum.ewm.main.model.events.EventState;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.Location;
import ru.practicum.ewm.main.model.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CompilationRepositoryTest {
    @Autowired
    private CompilationRepository compilationRepository;

    @Autowired
    private CompilationEventRepository compilationEventRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Events event1;

    @BeforeEach
    void prepare() {
        User initiator = entityManager.persistAndFlush(User.builder()
                .email("initiator@email.com")
                .name("initiator name")
                .build());

        Category category1 = entityManager.persistAndFlush(Category.builder()
                .name("category1234")
                .build());

        Location location1 = entityManager.persistAndFlush(Location.builder()
                .id(1L)
                .lon(55.0)
                .lat(55.0)
                .build());

        Events event = new Events();
        event.setTitle("title");
        event.setAnnotation("annotation12345");
        event.setDescription("descriptionsdfsdf");
        event.setInitiator(initiator);
        event.setCategory(category1);
        event.setLocation(location1);
        event.setEventDate(LocalDateTime.now().plusDays(2));
        event.setCreatedOn(LocalDateTime.now());
        event.setPublishedOn(LocalDateTime.now().plusDays(1));
        event.setState(EventState.PENDING);
        event.setPaid(false);
        event.setParticipantLimit(10);
        event.setId(1L);
        event1 = entityManager.persistAndFlush(event);
    }

    @Test
    void findByCompilationIdAndEventId_whenFound_thenReturnCompilationEvent() throws IOException {
        Compilation compilation = entityManager.persistAndFlush(Compilation.builder()
                .title("compilation title")
                .pinned(false)
                .build());
        CompilationEvent ce = new CompilationEvent(compilation, event1);
        CompilationEvent result = compilationEventRepository.save(ce);

        assertNotNull(result);
        assertEquals(compilation.getId(), result.getCompilation().getId());
        assertEquals(event1.getId(), result.getEvent().getId());
        assertEquals("compilation title", result.getCompilation().getTitle());
    }

    @Test
    void save_whenOk_thenReturnCompilationEvent() {
        Compilation compilation = entityManager.persistAndFlush(Compilation.builder()
                .title("compilation title")
                .pinned(false)
                .build());
        CompilationEvent ce = new CompilationEvent(compilation, event1);

        CompilationEvent result = compilationEventRepository.save(ce);

        assertNotNull(result);
        assertEquals("compilation title", result.getCompilation().getTitle());
        assertEquals(event1.getTitle(), result.getEvent().getTitle());
    }
}