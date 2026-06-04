package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthLoginRequest(
        @NotBlank(message = "Username e obrigatorio")
        @Size(max = 100, message = "Username deve ter no maximo 100 caracteres")
        String username,
        @NotBlank(message = "Password e obrigatoria")
        @Size(min = 6, max = 100, message = "Password deve ter entre 6 e 100 caracteres")
        String password
) {
}
