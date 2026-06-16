package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record InstructorRequest(
        @NotBlank(message = "Nome e obrigatorio")
        @Size(max = 100, message = "Nome deve ter no maximo 100 caracteres")
        String name,
        @NotBlank(message = "Licenca e obrigatoria")
        @Size(max = 50, message = "Licenca deve ter no maximo 50 caracteres")
        String license,
        @Size(max = 200, message = "Especializacao deve ter no maximo 200 caracteres")
        String specialization,
        @PositiveOrZero(message = "Horas de voo devem ser positivas")
        Integer flightHours,
        @Pattern(regexp = "active|inactive", message = "Status deve ser active ou inactive")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @Email(message = "Email invalido")
        @Size(max = 100, message = "Email deve ter no maximo 100 caracteres")
        String email,
        @Size(max = 20, message = "Telefone deve ter no maximo 20 caracteres")
        String phone
) {
}
