package pt.ipvc.estg.desktop.api.dto;

import java.util.List;

public record FoDashboardResponse(
        Integer studentId,
        String studentName,
        String courseName,
        Integer progress,
        Double flightHours,
        Double theoreticalHours,
        Integer requiredFlightHours,
        double completedFlightHours,
        long totalFlights,
        long upcomingFlights,
        long pendingPayments,
        double pendingAmount,
        List<FlightResponse> recentFlights,
        List<EvaluationResponse> recentEvaluations
) {
}
