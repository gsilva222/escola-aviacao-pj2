package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.Size;

public record FoProfileUpdateRequest(
        @Size(max = 20, message = "Telefone deve ter no maximo 20 caracteres")
        String phone,
        @Size(max = 255, message = "Morada deve ter no maximo 255 caracteres")
        String address,
        @Size(max = 50, message = "Nacionalidade deve ter no maximo 50 caracteres")
        String nationality
) {
}
