package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MaintenanceRequest(
        @NotNull(message = "AircraftId e obrigatorio")
        @Positive(message = "AircraftId deve ser positivo")
        Integer aircraftId,
        @NotBlank(message = "Tipo de manutencao e obrigatorio")
        @Size(max = 50, message = "Tipo de manutencao deve ter no maximo 50 caracteres")
        String maintenanceType,
        @NotBlank(message = "Descricao e obrigatoria")
        @Size(max = 500, message = "Descricao deve ter no maximo 500 caracteres")
        String description,
        @Size(max = 100, message = "Tecnico deve ter no maximo 100 caracteres")
        String technician,
        LocalDate startDate,
        LocalDate estimatedEndDate,
        LocalDate actualEndDate,
        @Pattern(regexp = "scheduled|in_progress|waiting_parts|completed", message = "Status deve ser scheduled, in_progress, waiting_parts ou completed")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @Pattern(regexp = "low|medium|high", message = "Prioridade deve ser low, medium ou high")
        @Size(max = 20, message = "Prioridade deve ter no maximo 20 caracteres")
        String priority,
        @PositiveOrZero(message = "Custo deve ser positivo")
        Double cost,
        @Size(max = 500, message = "Notas deve ter no maximo 500 caracteres")
        String notes
) {
}
