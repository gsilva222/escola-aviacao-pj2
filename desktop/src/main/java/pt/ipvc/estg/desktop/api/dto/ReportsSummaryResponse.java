package pt.ipvc.estg.desktop.api.dto;

import java.util.Map;

public record ReportsSummaryResponse(
        long totalStudents,
        long activeStudents,
        long suspendedStudents,
        long completedStudents,
        long totalCourses,
        long totalFlights,
        long scheduledFlights,
        long completedFlights,
        long totalAircraft,
        long operationalAircraft,
        long maintenanceAircraft,
        long groundedAircraft,
        long pendingPayments,
        long overduePayments,
        double totalPendingAmount,
        long activeMaintenances,
        Map<String, Long> studentsByCourse
) {
}
