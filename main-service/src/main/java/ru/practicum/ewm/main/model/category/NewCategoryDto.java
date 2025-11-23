package ru.practicum.ewm.main.model.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {
    @Size(max = 50, message = "Название категории не может превышать 50 символов")
    @NotBlank(message = "Название категории не может быть пустым")
    @NotNull(message = "Название категории обязательно")
    @Pattern(regexp = ".*\\S.*", message = "Название категории не может состоять только из пробелов")
    String name;
}
