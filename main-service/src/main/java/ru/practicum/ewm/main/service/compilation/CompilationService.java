package ru.practicum.ewm.main.service.compilation;

import ru.practicum.ewm.main.model.compilation.CompilationDto;
import ru.practicum.ewm.main.model.compilation.NewCompilationDto;
import ru.practicum.ewm.main.model.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.model.compilation.params.PublicCompilationSearchParams;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto newCompilationDto);

    CompilationDto patch(UpdateCompilationRequest updateCompilationRequestDto, Long compId);

    void delete(Long compId);

    CompilationDto findById(Long compId);

    List<CompilationDto> findCompilations(PublicCompilationSearchParams params);
}
