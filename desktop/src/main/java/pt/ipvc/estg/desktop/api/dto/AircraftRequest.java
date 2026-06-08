package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record AircraftRequest(
        String registration,
        String model,
        String type,
        Integer manufYear,
        String status,
        Double flightHours,
        LocalDate lastMaintenance,
        LocalDate nextMaintenance,
        String location,
        Integer fuelLevel,
        String notes
) {
}
