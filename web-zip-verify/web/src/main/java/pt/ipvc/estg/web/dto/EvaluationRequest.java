package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EvaluationRequest(
        @NotNull(message = "StudentId e obrigatorio")
        @Positive(message = "StudentId deve ser positivo")
        Integer studentId,
        @NotNull(message = "CourseId e obrigatorio")
        @Positive(message = "CourseId deve ser positivo")
        Integer courseId,
        @NotBlank(message = "Nome do exame e obrigatorio")
        @Size(max = 200, message = "Nome do exame deve ter no maximo 200 caracteres")
        String examName,
        LocalDate evaluationDate,
        @PositiveOrZero(message = "Pontuacao deve ser positiva")
        Integer score,
        @Positive(message = "Pontuacao maxima deve ser superior a zero")
        Integer maxScore,
        @Pattern(regexp = "passed|failed|scheduled", message = "Status deve ser passed, failed ou scheduled")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @Pattern(regexp = "theoretical|practical|simulator", message = "Tipo de avaliacao deve ser theoretical, practical ou simulator")
        @Size(max = 50, message = "Tipo de avaliacao deve ter no maximo 50 caracteres")
        String evaluationType,
        @Size(max = 500, message = "Notas deve ter no maximo 500 caracteres")
        String notes
) {
}
