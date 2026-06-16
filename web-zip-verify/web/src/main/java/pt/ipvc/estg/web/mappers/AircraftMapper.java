package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.AircraftRequest;
import pt.ipvc.estg.web.dto.AircraftResponse;

public final class AircraftMapper {
    private AircraftMapper() {}

    public static Aircraft toEntity(AircraftRequest request) {
        Aircraft aircraft = new Aircraft(request.registration(), request.model(), request.type());
        aircraft.setManufYear(request.manufYear());
        if (request.status() != null) {
            aircraft.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.AIRCRAFT_STATUSES));
        }
        aircraft.setFlightHours(request.flightHours());
        aircraft.setLastMaintenance(request.lastMaintenance());
        aircraft.setNextMaintenance(request.nextMaintenance());
        aircraft.setLocation(request.location());
        aircraft.setFuelLevel(request.fuelLevel());
        aircraft.setNotes(request.notes());
        return aircraft;
    }

    public static AircraftResponse toResponse(Aircraft aircraft) {
        if (aircraft == null) {
            return null;
        }

        return new AircraftResponse(
                aircraft.getId(),
                aircraft.getRegistration(),
                aircraft.getModel(),
                aircraft.getType(),
                aircraft.getManufYear(),
                aircraft.getStatus(),
                aircraft.getFlightHours(),
                aircraft.getLastMaintenance(),
                aircraft.getNextMaintenance(),
                aircraft.getLocation(),
                aircraft.getFuelLevel(),
                aircraft.getNotes()
        );
    }
}
