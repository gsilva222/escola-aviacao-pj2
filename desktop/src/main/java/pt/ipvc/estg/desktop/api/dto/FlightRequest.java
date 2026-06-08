package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FlightRequest(
        LocalDate flightDate,
        LocalTime flightTime,
        Double duration,
        Integer studentId,
        Integer instructorId,
        Integer aircraftId,
        String origin,
        String destination,
        String flightType,
        String status,
        String objectives,
        String notes,
        String grade
) {
}
