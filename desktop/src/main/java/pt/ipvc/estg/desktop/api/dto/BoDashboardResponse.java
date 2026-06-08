package pt.ipvc.estg.desktop.api.dto;

import java.util.List;

public record BoDashboardResponse(
        ReportsSummaryResponse reports,
        long flightsToday,
        long completedFlightsToday,
        long scheduledFlightsToday,
        double flightHoursThisMonth,
        double revenueThisMonth,
        List<ActivityItem> recentActivity
) {
    public record ActivityItem(String icon, String title, String subtitle, String type) {
    }
}
