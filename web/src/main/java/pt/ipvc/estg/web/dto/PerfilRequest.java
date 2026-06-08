package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 500) String descricao
) {
}
