package ru.practicum.ewm.main.service.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.mapper.category.CategoryDtoMapper;
import ru.practicum.ewm.main.mapper.category.NewCategoryDtoMapper;
import ru.practicum.ewm.main.model.category.Category;
import ru.practicum.ewm.main.model.category.CategoryDto;
import ru.practicum.ewm.main.model.category.NewCategoryDto;
import ru.practicum.ewm.main.repository.category.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategory) {
        if (categoryRepository.existsByName(newCategory.getName().trim())) {
            throw new ConflictException(
                    "Категория с именем '" + newCategory.getName() + "' уже существует"
            );
        }
        return CategoryDtoMapper.toDto(
                categoryRepository.save(NewCategoryDtoMapper.toModel(newCategory))
        );
    }

    @Override
    public void removeCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, NewCategoryDto updateCategory) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        String newName = updateCategory.getName().trim();
        String currentName = category.getName();
        if (!currentName.equals(newName)) {
            if (categoryRepository.existsByName(newName)) {
                throw new ConflictException(
                        "Категория с именем '" + newName + "' уже существует"
                );
            }
            category.setName(newName);
        }

        return CategoryDtoMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findAllCategories(Long from, Long size) {
        return categoryRepository.findCategoriesWithParameters(from, size)
                .stream()
                .map(CategoryDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategoryById(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена"));
        return CategoryDtoMapper.toDto(category);
    }

    @Override
    public Category findCategoryEntityById(Long categoryId) {
        log.info("Поиск категории (entity) id={}", categoryId);

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Категория id={} не найдена", categoryId);
                    return new NotFoundException("Category with id=" + categoryId + " was not found");
                });
    }


}
