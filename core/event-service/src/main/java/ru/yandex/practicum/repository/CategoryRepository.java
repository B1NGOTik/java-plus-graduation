package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT * FROM event.categories ORDER BY id LIMIT ?2 OFFSET ?1",
            nativeQuery = true)
    List<Category> findCategoriesWithParameters(Long from, Long size);

    boolean existsByName(String name);
}
