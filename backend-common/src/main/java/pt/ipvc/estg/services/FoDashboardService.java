package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.FoDashboardSummary;
import pt.ipvc.estg.domain.FoHoursSummary;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.EvaluationRepository;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.util.FlightHoursCalculator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class FoDashboardService {

    private final Supplier<Student> currentStudentSupplier;
    private final FlightRepository flightRepository;
    private final EvaluationRepository evaluationRepository;
    private final PaymentSummaryService paymentSummaryService;

    public FoDashboardService(Supplier<Student> currentStudentSupplier,
                              FlightRepository flightRepository,
                              EvaluationRepository evaluationRepository,
                              PaymentSummaryService paymentSummaryService) {
        this.currentStudentSupplier = currentStudentSupplier;
        this.flightRepository = flightRepository;
        this.evaluationRepository = evaluationRepository;
        this.paymentSummaryService = paymentSummaryService;
    }

    public FoDashboardSummary buildDashboard() {
        Student student = currentStudentSupplier.get();
        int studentId = student.getId();
        List<Flight> flights = flightRepository.findByStudent(studentId);
        List<Evaluation> evaluations = evaluationRepository.findByStudent(studentId);

        int requiredHours = student.getCourse() != null && student.getCourse().getFlightHours() != null
                ? student.getCourse().getFlightHours() : 45;
        double completed = FlightHoursCalculator.completedHours(flights);
        var paymentSummary = paymentSummaryService.summarizeForStudent(studentId);

        List<Flight> recentFlights = flights.stream()
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        List<Evaluation> recentEvaluations = evaluations.stream()
                .sorted(Comparator.comparing(Evaluation::getEvaluationDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        long upcoming = flights.stream()
                .filter(f -> "scheduled".equals(FlightHoursCalculator.normalizeStatus(f)))
                .count();

        String courseName = student.getCourse() != null ? student.getCourse().getName() : null;

        return new FoDashboardSummary(
                studentId,
                student.getName(),
                courseName,
                student.getProgress(),
                student.getFlightHours(),
                student.getTheoreticalHours(),
                requiredHours,
                completed,
                flights.size(),
                upcoming,
                paymentSummary.pendingCount(),
                paymentSummary.totalPending(),
                recentFlights,
                recentEvaluations
        );
    }

    public FoHoursSummary buildHoursSummary() {
        Student student = currentStudentSupplier.get();
        List<Flight> flights = flightRepository.findByStudent(student.getId());
        double completed = FlightHoursCalculator.completedHours(flights);
        double required = student.getCourse() != null && student.getCourse().getFlightHours() != null
                ? student.getCourse().getFlightHours()
                : 45.0;
        double remaining = Math.max(0, required - completed);
        int progress = required <= 0 ? 0 : (int) Math.min(100, Math.round((completed / required) * 100));

        return new FoHoursSummary(
                completed,
                FlightHoursCalculator.localHours(flights),
                FlightHoursCalculator.navigationHours(flights),
                flights.size(),
                completed / 6.0,
                required,
                remaining,
                progress
        );
    }
}
