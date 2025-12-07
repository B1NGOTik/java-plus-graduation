package ru.practicum.ewm.main.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.model.category.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT * FROM categories ORDER BY id LIMIT ?2 OFFSET ?1",
            nativeQuery = true)
    List<Category> findCategoriesWithParameters(Long from, Long size);

    boolean existsByName(String name);
}
