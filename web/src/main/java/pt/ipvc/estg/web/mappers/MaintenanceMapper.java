package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.MaintenanceRequest;
import pt.ipvc.estg.web.dto.MaintenanceResponse;

public final class MaintenanceMapper {
    private MaintenanceMapper() {}

    public static Maintenance toEntity(MaintenanceRequest request) {
        Aircraft aircraft = new Aircraft();
        aircraft.setId(request.aircraftId());
        Maintenance maintenance = new Maintenance(aircraft, request.maintenanceType(), request.description());
        maintenance.setTechnician(request.technician());
        maintenance.setStartDate(request.startDate());
        maintenance.setEstimatedEndDate(request.estimatedEndDate());
        maintenance.setActualEndDate(request.actualEndDate());
        if (request.status() != null) {
            maintenance.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.MAINTENANCE_STATUSES));
        }
        if (request.priority() != null) {
            maintenance.setPriority(BusinessRules.requireAllowed("Prioridade", request.priority(), BusinessRules.MAINTENANCE_PRIORITIES));
        }
        maintenance.setCost(request.cost());
        maintenance.setNotes(request.notes());
        return maintenance;
    }

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
