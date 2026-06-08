package pt.ipvc.estg.domain;

import java.util.Map;

public record ReportsSummary(
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
