package pt.ipvc.estg.domain;

import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;

import java.util.List;

public record FoDashboardSummary(
        int studentId,
        String studentName,
        String courseName,
        Integer progress,
        Double flightHours,
        Double theoreticalHours,
        int requiredHours,
        double completedHours,
        int totalFlights,
        long upcomingFlights,
        long pendingPaymentCount,
        double totalPending,
        List<Flight> recentFlights,
        List<Evaluation> recentEvaluations
) {
}
