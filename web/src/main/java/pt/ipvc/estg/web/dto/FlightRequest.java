package pt.ipvc.estg.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record FlightRequest(
        @NotNull(message = "Data do voo e obrigatoria")
        LocalDate flightDate,
        LocalTime flightTime,
        @Positive(message = "Duracao deve ser superior a zero")
        Double duration,
        @NotNull(message = "StudentId e obrigatorio")
        @Positive(message = "StudentId deve ser positivo")
        Integer studentId,
        @NotNull(message = "InstructorId e obrigatorio")
        @Positive(message = "InstructorId deve ser positivo")
        Integer instructorId,
        @NotNull(message = "AircraftId e obrigatorio")
        @Positive(message = "AircraftId deve ser positivo")
        Integer aircraftId,
        @Size(max = 10, message = "Origem deve ter no maximo 10 caracteres")
        @Pattern(regexp = "[A-Z]{4}|", message = "Origem deve ser um codigo ICAO com 4 letras maiusculas")
        String origin,
        @Size(max = 10, message = "Destino deve ter no maximo 10 caracteres")
        @Pattern(regexp = "[A-Z]{4}|", message = "Destino deve ser um codigo ICAO com 4 letras maiusculas")
        String destination,
        @Size(max = 50, message = "Tipo de voo deve ter no maximo 50 caracteres")
        String flightType,
        @Pattern(regexp = "scheduled|completed|cancelled", message = "Status deve ser scheduled, completed ou cancelled")
        @Size(max = 20, message = "Status deve ter no maximo 20 caracteres")
        String status,
        @Size(max = 500, message = "Objetivos deve ter no maximo 500 caracteres")
        String objectives,
        @Size(max = 500, message = "Notas deve ter no maximo 500 caracteres")
        String notes,
        @Size(max = 5, message = "Nota deve ter no maximo 5 caracteres")
        String grade
) {
}
