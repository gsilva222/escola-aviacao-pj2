package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.web.dto.MaintenanceResponse;

public final class MaintenanceMapper {
    private MaintenanceMapper() {}

    public static MaintenanceResponse toResponse(Maintenance maintenance) {
        if (maintenance == null) {
            return null;
        }

        Aircraft aircraft = maintenance.getAircraft();

        return new MaintenanceResponse(
                maintenance.getId(),
                aircraft != null ? aircraft.getId() : null,
                aircraft != null ? aircraft.getRegistration() : null,
                maintenance.getMaintenanceType(),
                maintenance.getDescription(),
                maintenance.getTechnician(),
                maintenance.getStartDate(),
                maintenance.getEstimatedEndDate(),
                maintenance.getActualEndDate(),
                maintenance.getStatus(),
                maintenance.getPriority(),
                maintenance.getCost(),
                maintenance.getNotes()
        );
    }
}
