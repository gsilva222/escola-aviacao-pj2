package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
        @NotBlank(message = "Username e obrigatorio")
        @Size(max = 100, message = "Username deve ter no maximo 100 caracteres")
        String username,
        @NotBlank(message = "Password e obrigatoria")
        @Size(min = 6, max = 100, message = "Password deve ter entre 6 e 100 caracteres")
        String password,
        @NotBlank(message = "Role e obrigatorio")
        @Pattern(regexp = "ADMIN|STUDENT|admin|student", message = "Role deve ser ADMIN ou STUDENT")
        @Size(max = 20, message = "Role deve ter no maximo 20 caracteres")
        String role,
        @Positive(message = "StudentId deve ser positivo")
        Integer studentId
) {
}
