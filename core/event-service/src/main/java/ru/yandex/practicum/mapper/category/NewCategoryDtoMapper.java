package ru.yandex.practicum.mapper.category;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.event.category.NewCategoryDto;
import ru.yandex.practicum.model.Category;

@UtilityClass
public class NewCategoryDtoMapper {
    public static Category toModel(NewCategoryDto newCategory) {
        return Category.builder()
                .name(newCategory.getName())
                .build();
    }
}
