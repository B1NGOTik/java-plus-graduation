package ru.yandex.practicum.service.category;

import ru.yandex.practicum.event.category.CategoryDto;
import ru.yandex.practicum.event.category.NewCategoryDto;
import ru.yandex.practicum.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategory);

    void removeCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto updateCategory);

    List<CategoryDto> findAllCategories(Long from, Long size);

    CategoryDto findCategoryById(Long categoryId);

    Category findCategoryEntityById(Long categoryId);
}
