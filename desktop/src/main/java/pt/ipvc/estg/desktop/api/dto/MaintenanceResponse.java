package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record MaintenanceResponse(
        Integer id,
        Integer aircraftId,
        String aircraftRegistration,
        String maintenanceType,
        String description,
        String technician,
        LocalDate startDate,
        LocalDate estimatedEndDate,
        LocalDate actualEndDate,
        String status,
        String priority,
        Double cost,
        String notes
) {
}
