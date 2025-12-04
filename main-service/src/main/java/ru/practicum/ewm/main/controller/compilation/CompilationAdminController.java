package ru.practicum.ewm.main.controller.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.model.compilation.CompilationDto;
import ru.practicum.ewm.main.model.compilation.NewCompilationDto;
import ru.practicum.ewm.main.model.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.main.service.compilation.CompilationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> save(@RequestBody @Valid NewCompilationDto dto) {
        CompilationDto result = compilationService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> patch(@RequestBody UpdateCompilationRequest dto,
                                                @PathVariable Long compId) {
        CompilationDto result = compilationService.patch(dto, compId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<String> delete(@PathVariable Long compId) {
        compilationService.delete(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Подборка удалена");
    }

}
