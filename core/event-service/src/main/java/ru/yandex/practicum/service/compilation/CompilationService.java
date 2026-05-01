package ru.yandex.practicum.service.compilation;


import ru.yandex.practicum.event.compilation.CompilationDto;
import ru.yandex.practicum.event.compilation.NewCompilationDto;
import ru.yandex.practicum.event.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.event.compilation.params.PublicCompilationSearchParams;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    CompilationDto patch(UpdateCompilationRequest updateCompilationRequestDto, Long compId);

    void delete(Long compId);

    CompilationDto findById(Long compId);

    List<CompilationDto> findCompilations(PublicCompilationSearchParams params);
}
