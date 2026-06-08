package pt.ipvc.estg.domain;

import java.util.List;

public record BoDashboardSummary(
        ReportsSummary reports,
        long flightsToday,
        long completedFlightsToday,
        long scheduledFlightsToday,
        double flightHoursThisMonth,
        double revenueThisMonth,
        List<BoActivityItem> recentActivity
) {
    public record BoActivityItem(String icon, String title, String subtitle, String type) {
    }
}
