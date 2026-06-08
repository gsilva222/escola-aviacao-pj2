package pt.ipvc.estg.domain;

public record FoHoursSummary(
        double completedHours,
        double localHours,
        double navigationHours,
        int totalFlights,
        double averagePerFlight,
        double requiredHours,
        double remainingHours,
        int progressPercent
) {
}
