package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank(message = "Nome do curso e obrigatorio")
        @Size(max = 100, message = "Nome do curso deve ter no maximo 100 caracteres")
        String name,
        @Size(max = 50, message = "Duracao deve ter no maximo 50 caracteres")
        String duration,
        @Positive(message = "Horas de voo devem ser superiores a zero")
        Integer flightHours,
        @Positive(message = "Horas teoricas devem ser superiores a zero")
        Integer theoreticalHours,
        @PositiveOrZero(message = "Preco deve ser positivo")
        Double price,
        @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
        String description
) {
}
