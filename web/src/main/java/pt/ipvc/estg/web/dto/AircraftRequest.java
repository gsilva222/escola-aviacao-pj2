package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AircraftRequest(
        @NotBlank(message = "Matricula e obrigatoria")
        @Size(max = 20, message = "Matricula deve ter no maximo 20 caracteres")
        String registration,
        @NotBlank(message = "Modelo e obrigatorio")
        @Size(max = 100, message = "Modelo deve ter no maximo 100 caracteres")
        String model,
        @NotBlank(message = "Tipo e obrigatorio")
        @Size(max = 50, message = "Tipo deve ter no maximo 50 caracteres")
        String type,
        @Min(value = 1903, message = "Ano de fabrico deve ser valido")
        @Max(value = 2100, message = "Ano de fabrico deve ser valido")
        Integer manufYear,
        @Pattern(regexp = "operational|maintenance|grounded", message = "Status deve ser operational, maintenance ou grounded")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @PositiveOrZero(message = "Horas de voo devem ser positivas")
        Double flightHours,
        LocalDate lastMaintenance,
        LocalDate nextMaintenance,
        @Size(max = 100, message = "Localizacao deve ter no maximo 100 caracteres")
        String location,
        @PositiveOrZero(message = "Nivel de combustivel deve ser positivo")
        @Max(value = 100, message = "Nivel de combustivel deve estar entre 0 e 100")
        Integer fuelLevel,
        @Size(max = 500, message = "Notas deve ter no maximo 500 caracteres")
        String notes
) {
}
