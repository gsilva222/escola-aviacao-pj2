package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.web.dto.AircraftResponse;

public final class AircraftMapper {
    private AircraftMapper() {}

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
