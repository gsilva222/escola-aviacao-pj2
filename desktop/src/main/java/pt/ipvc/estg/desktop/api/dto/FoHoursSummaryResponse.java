package pt.ipvc.estg.desktop.api.dto;

public record FoHoursSummaryResponse(
        double totalCompletedHours,
        double localHours,
        double navigationHours,
        long totalFlights,
        double monthlyAverageHours,
        double requiredHours,
        double remainingHours,
        int progressPercent
) {
}
