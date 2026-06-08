package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record MaintenanceRequest(
        Integer aircraftId,
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
