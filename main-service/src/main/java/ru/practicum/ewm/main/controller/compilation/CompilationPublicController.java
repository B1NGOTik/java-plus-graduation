package ru.practicum.ewm.main.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.compilation.CompilationDto;
import ru.practicum.ewm.main.model.compilation.params.PublicCompilationSearchParams;
import ru.practicum.ewm.main.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    ResponseEntity<CompilationDto> findById(@PathVariable Long compId) {
        CompilationDto result = compilationService.findById(compId);
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    ResponseEntity<List<CompilationDto>> findCompilations(@ModelAttribute @Valid PublicCompilationSearchParams params) {
        List<CompilationDto> result = compilationService.findCompilations(params);
        return ResponseEntity.ok(result);
    }

}
