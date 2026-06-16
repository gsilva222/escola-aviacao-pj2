package pt.ipvc.estg.web.dto;

import java.util.List;

public record BoDashboardResponse(
        ReportsSummaryResponse reports,
        long flightsToday,
        long completedFlightsToday,
        long scheduledFlightsToday,
        double flightHoursThisMonth,
        double revenueThisMonth,
        List<BoActivityItemResponse> recentActivity
) {
    public record BoActivityItemResponse(String icon, String title, String subtitle, String type) {
    }
}
