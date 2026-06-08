package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FlightResponse(
        Integer id,
        LocalDate flightDate,
        LocalTime flightTime,
        Double duration,
        Integer studentId,
        String studentName,
        Integer instructorId,
        String instructorName,
        Integer aircraftId,
        String aircraftRegistration,
        String origin,
        String destination,
        String flightType,
        String status,
        String objectives,
        String notes,
        String grade
) {
}
