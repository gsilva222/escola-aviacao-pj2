package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.BoDashboardSummary;
import pt.ipvc.estg.domain.ReportsSummary;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.repositories.EvaluationRepository;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.repositories.PaymentRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BoDashboardService {

    private final ReportsService reportsService;
    private final FlightRepository flightRepository;
    private final PaymentRepository paymentRepository;

    public BoDashboardService(ReportsService reportsService,
                              FlightRepository flightRepository,
                              PaymentRepository paymentRepository) {
        this.reportsService = reportsService;
        this.flightRepository = flightRepository;
        this.paymentRepository = paymentRepository;
    }

    public BoDashboardSummary buildDashboard() {
        ReportsSummary reports = reportsService.buildSummary();
        LocalDate today = LocalDate.now();
        YearMonth month = YearMonth.now();

        List<Flight> flights = flightRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        long flightsToday = flights.stream().filter(f -> today.equals(f.getFlightDate())).count();
        long completedToday = flights.stream()
                .filter(f -> today.equals(f.getFlightDate()) && "completed".equals(f.getStatus()))
                .count();
        long scheduledToday = flights.stream()
                .filter(f -> today.equals(f.getFlightDate()) && "scheduled".equals(f.getStatus()))
                .count();

        double monthHours = flights.stream()
                .filter(f -> f.getFlightDate() != null && YearMonth.from(f.getFlightDate()).equals(month))
                .filter(f -> "completed".equals(f.getStatus()))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();

        double monthRevenue = payments.stream()
                .filter(p -> "paid".equals(p.getStatus()) && p.getPaidDate() != null)
                .filter(p -> YearMonth.from(p.getPaidDate()).equals(month))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        return new BoDashboardSummary(
                reports,
                flightsToday,
                completedToday,
                scheduledToday,
                monthHours,
                monthRevenue,
                buildRecentActivity(flights, payments, 6)
        );
    }

    private List<BoDashboardSummary.BoActivityItem> buildRecentActivity(
            List<Flight> flights, List<Payment> payments, int limit) {
        List<BoDashboardSummary.BoActivityItem> items = new ArrayList<>();

        flights.stream()
                .filter(f -> "completed".equals(f.getStatus()))
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .forEach(f -> items.add(new BoDashboardSummary.BoActivityItem(
                        "flight",
                        "Voo " + (f.getAircraft() != null ? f.getAircraft().getRegistration() : "") + " concluido",
                        (f.getStudent() != null ? f.getStudent().getName() : "Aluno") + " · " + formatRelative(f.getFlightDate()),
                        "success"
                )));

        payments.stream()
                .filter(p -> "paid".equals(p.getStatus()))
                .sorted(Comparator.comparing(Payment::getPaidDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .forEach(p -> items.add(new BoDashboardSummary.BoActivityItem(
                        "payment",
                        "Pagamento recebido",
                        String.format("EUR %.0f · %s", p.getAmount() != null ? p.getAmount() : 0.0, formatRelative(p.getPaidDate())),
                        "success"
                )));

        return items.stream().limit(limit).toList();
    }

    private String formatRelative(LocalDate date) {
        if (date == null) {
            return "recente";
        }
        long days = LocalDate.now().toEpochDay() - date.toEpochDay();
        if (days <= 0) {
            return "hoje";
        }
        if (days == 1) {
            return "ha 1 dia";
        }
        return "ha " + days + " dias";
    }
}
