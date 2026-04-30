package ru.yandex.practicum.mapper.category;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.event.category.CategoryDto;
import ru.yandex.practicum.model.Category;

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
