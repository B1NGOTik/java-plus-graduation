package ru.practicum.ewm.main.service.category;


import ru.practicum.ewm.main.model.category.Category;
import ru.practicum.ewm.main.model.category.CategoryDto;
import ru.practicum.ewm.main.model.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategory);

    void removeCategory(Long categoryId);

    CategoryDto updateCategory(Long categoryId, NewCategoryDto updateCategory);

    List<CategoryDto> findAllCategories(Long from, Long size);

    CategoryDto findCategoryById(Long categoryId);

    Category findCategoryEntityById(Long categoryId);
}
