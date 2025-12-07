package ru.practicum.ewm.main.controller.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.model.category.CategoryDto;
import ru.practicum.ewm.main.model.category.NewCategoryDto;
import ru.practicum.ewm.main.service.category.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private static final String ADMIN_CATEGORIES_PATH = "/admin/categories";
    private static final String PUBLIC_CATEGORIES_PATH = "/categories";
    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(ADMIN_CATEGORIES_PATH)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto newCategory) {
        return categoryService.addCategory(newCategory);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(ADMIN_CATEGORIES_PATH + "/{categoryId}")
    public void removeCategory(@PathVariable Long categoryId) {
        try {
            categoryService.removeCategory(categoryId);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @PatchMapping(ADMIN_CATEGORIES_PATH + "/{categoryId}")
    public CategoryDto updateCategory(@PathVariable Long categoryId,
                                      @Valid @RequestBody NewCategoryDto category) {
        return categoryService.updateCategory(categoryId, category);
    }

    @GetMapping(PUBLIC_CATEGORIES_PATH)
    public List<CategoryDto> findAllCategories(@RequestParam(defaultValue = "0") Long from,
                                               @RequestParam(defaultValue = "10") Long size) {
        return categoryService.findAllCategories(from, size);
    }

    @GetMapping(PUBLIC_CATEGORIES_PATH + "/{categoryId}")
    public CategoryDto findCategoryById(@PathVariable Long categoryId) {
        return categoryService.findCategoryById(categoryId);
    }
}
