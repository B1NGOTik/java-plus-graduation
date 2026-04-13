package ru.practicum.ewm.main.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    private String email;
}
