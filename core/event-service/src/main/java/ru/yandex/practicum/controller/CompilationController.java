package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.compilation.CompilationDto;
import ru.yandex.practicum.event.compilation.NewCompilationDto;
import ru.yandex.practicum.event.compilation.UpdateCompilationRequest;
import ru.yandex.practicum.event.compilation.params.PublicCompilationSearchParams;
import ru.yandex.practicum.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    public ResponseEntity<CompilationDto> save(@RequestBody @Valid NewCompilationDto dto) {
        CompilationDto result = compilationService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public ResponseEntity<CompilationDto> patch(@RequestBody UpdateCompilationRequest dto,
                                                @PathVariable Long compId) {
        CompilationDto result = compilationService.patch(dto, compId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    public ResponseEntity<String> delete(@PathVariable Long compId) {
        compilationService.delete(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Подборка удалена");
    }

    @GetMapping("/compilations/{compId}")
    ResponseEntity<CompilationDto> findById(@PathVariable Long compId) {
        CompilationDto result = compilationService.findById(compId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/compilations")
    ResponseEntity<List<CompilationDto>> findCompilations(@ModelAttribute @Valid PublicCompilationSearchParams params) {
        List<CompilationDto> result = compilationService.findCompilations(params);
        return ResponseEntity.ok(result);
    }

}
