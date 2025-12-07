package ru.practicum.ewm.main.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.ValidationException;
import ru.practicum.ewm.main.mapper.compilation.CompilationMapper;
import ru.practicum.ewm.main.mapper.events.EventsMapper;
import ru.practicum.ewm.main.model.compilation.*;
import ru.practicum.ewm.main.model.compilation.params.PublicCompilationSearchParams;
import ru.practicum.ewm.main.model.events.Events;
import ru.practicum.ewm.main.model.events.dto.EventShortDto;
import ru.practicum.ewm.main.repository.compilation.CompilationEventRepository;
import ru.practicum.ewm.main.repository.compilation.CompilationRepository;
import ru.practicum.ewm.main.repository.events.EventsRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationEventRepository compilationEventRepository;
    private final EventsRepository eventsRepository;
    private final EventsMapper eventsMapper;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationDto newDto) {
        List<Events> events = eventsRepository.findByIdIn(List.copyOf(newDto.getEvents()));
        if (newDto.getEvents().size() != events.size()) {
            throw new NotFoundException("Найдены не все события, добавляемые в подборку");
        }

        try {
            Compilation savedCompilation = compilationRepository.save(CompilationMapper.toModel(newDto));
            List<CompilationEvent> list = events.stream().map(e -> new CompilationEvent(savedCompilation, e)).toList();
            compilationEventRepository.saveAll(list);

            return CompilationMapper.toDto(savedCompilation,
                    events.stream().map(eventsMapper::toShortDto).toList());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public CompilationDto patch(UpdateCompilationRequest updateDto, Long compId) {
        if (updateDto.getTitle() != null && (updateDto.getTitle().length() > 50 || updateDto.getTitle().isEmpty())) {
            throw new ValidationException("заголовок подборки должен быть в диапазоне от 1 до 50 символов");
        }
        Compilation updateCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("не найдена подборка с id " + compId));

        if (updateDto.getPinned() != null) {
            updateCompilation.setPinned(updateDto.getPinned());
        }

        if (updateDto.getTitle() != null) {
            updateCompilation.setTitle(updateDto.getTitle());
        }

        List<Events> events;
        if (updateDto.getEvents() != null) {
            events = eventsRepository.findByIdIn(List.copyOf(updateDto.getEvents()));
            if (updateDto.getEvents().size() != events.size()) {
                throw new NotFoundException("Найдены не все события, добавляемые в подборку");
            }

        } else {
            events = eventsRepository.findByIdIn(compilationEventRepository.findEventIdsByCompilationId(compId));
        }

        try {
            Compilation savedCompilation = compilationRepository.save(updateCompilation);
            List<CompilationEvent> list = events.stream().map(e -> new CompilationEvent(savedCompilation, e)).toList();
            compilationEventRepository.saveAll(list);
            return CompilationMapper.toDto(savedCompilation,
                    events.stream().map(eventsMapper::toShortDto).toList());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        Compilation result = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("не найдена подборка с id " + compId));

        compilationRepository.delete(result);
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("не найдена подборка с id " + compId));

        List<Events> events = eventsRepository.findByIdIn(compilationEventRepository.findEventIdsByCompilationId(compId));
        return CompilationMapper.toDto(compilation,
                events.stream().map(eventsMapper::toShortDto).toList());
    }

    @Override
    public List<CompilationDto> findCompilations(PublicCompilationSearchParams params) {
        Pageable pageable = PageRequest.of(params.getFrom() / params.getSize(), params.getSize());
        Page<Compilation> compilationPage = compilationRepository.findByPinned(params.getPinned(), pageable);

        List<Compilation> compilations = compilationPage.getContent();
        if (compilations.isEmpty()) {
            return List.of();
        }

        List<Long> cIds = compilations.stream()
                .map(Compilation::getId)
                .toList();

        List<CompilationEvent> compilationEvents =
                compilationEventRepository
                        .findByCompilationIds(cIds);

        Map<Long, List<Events>> eventsByCompilationId = compilationEvents.stream()
                .collect(Collectors.groupingBy(
                        ce -> ce.getCompilation().getId(),
                        Collectors.mapping(CompilationEvent::getEvent, Collectors.toList())
                ));


        return compilations.stream()
                .map(c -> {
                    List<Events> events = eventsByCompilationId
                            .getOrDefault(c.getId(), List.of());

                    List<EventShortDto> eventShortDtos = events.stream()
                            .map(eventsMapper::toShortDto)
                            .toList();

                    return CompilationMapper.toDto(c, eventShortDtos);
                })
                .toList();
    }
}
