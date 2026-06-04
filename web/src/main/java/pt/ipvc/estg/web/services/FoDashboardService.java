package pt.ipvc.estg.web.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.EvaluationResponse;
import pt.ipvc.estg.web.dto.FlightResponse;
import pt.ipvc.estg.web.dto.FoDashboardResponse;
import pt.ipvc.estg.web.dto.FoHoursSummaryResponse;
import pt.ipvc.estg.web.mappers.EvaluationMapper;
import pt.ipvc.estg.web.mappers.FlightMapper;
import pt.ipvc.estg.web.repositories.EvaluationRepository;
import pt.ipvc.estg.web.repositories.FlightRepository;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FoDashboardService {

    private final StudentScopeService studentScopeService;
    private final FlightRepository flightRepository;
    private final EvaluationRepository evaluationRepository;
    private final PaymentSummaryService paymentSummaryService;

    public FoDashboardService(StudentScopeService studentScopeService,
                              FlightRepository flightRepository,
                              EvaluationRepository evaluationRepository,
                              PaymentSummaryService paymentSummaryService) {
        this.studentScopeService = studentScopeService;
        this.flightRepository = flightRepository;
        this.evaluationRepository = evaluationRepository;
        this.paymentSummaryService = paymentSummaryService;
    }

    public FoDashboardResponse buildDashboard() {
        Student student = studentScopeService.requireCurrentStudent();
        int studentId = student.getId();
        List<Flight> flights = flightRepository.findByStudent_Id(studentId);
        List<Evaluation> evaluations = evaluationRepository.findByStudent_Id(studentId);

        int requiredHours = student.getCourse() != null && student.getCourse().getFlightHours() != null
                ? student.getCourse().getFlightHours() : 45;
        double completed = FlightHoursCalculator.completedHours(flights);
        var paymentSummary = paymentSummaryService.summarizeForStudent(studentId);

        List<FlightResponse> recentFlights = flights.stream()
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(FlightMapper::toResponse)
                .toList();

        List<EvaluationResponse> recentEvaluations = evaluations.stream()
                .sorted(Comparator.comparing(Evaluation::getEvaluationDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(EvaluationMapper::toResponse)
                .toList();

        long upcoming = flights.stream()
                .filter(f -> "scheduled".equals(FlightHoursCalculator.normalizeStatus(f)))
                .count();

        String courseName = student.getCourse() != null ? student.getCourse().getName() : null;

        return new FoDashboardResponse(
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

    public FoHoursSummaryResponse buildHoursSummary() {
        Student student = studentScopeService.requireCurrentStudent();
        List<Flight> flights = flightRepository.findByStudent_Id(student.getId());
        double completed = FlightHoursCalculator.completedHours(flights);
        double required = student.getCourse() != null && student.getCourse().getFlightHours() != null
                ? student.getCourse().getFlightHours()
                : 45.0;
        double remaining = Math.max(0, required - completed);
        int progress = required <= 0 ? 0 : (int) Math.min(100, Math.round((completed / required) * 100));

        return new FoHoursSummaryResponse(
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
