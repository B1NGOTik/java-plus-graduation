package ru.practicum.ewm.main.mapper.category;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.main.model.category.Category;
import ru.practicum.ewm.main.model.category.CategoryDto;

@UtilityClass
public class CategoryDtoMapper {

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toModel(CategoryDto dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
