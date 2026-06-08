package pt.ipvc.estg.desktop.services;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.dto.ReportsSummaryResponse;
import pt.ipvc.estg.desktop.api.dto.StudentDocumentResponse;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.desktop.controllers.InstructorController;
import pt.ipvc.estg.desktop.controllers.PaymentController;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Payment;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoAdminService {

    private final BoApiService boApi = new BoApiService();
    private final FlightController flightController = new FlightController();
    private final PaymentController paymentController = new PaymentController();
    private final EvaluationController evaluationController = new EvaluationController();
    private final InstructorController instructorController = new InstructorController();

    public boolean useApi() {
        return BoDataAccess.useApi();
    }

    public ReportsSummaryResponse getReportsSummary() {
        if (!useApi()) {
            return null;
        }
        try {
            return boApi.getReportsSummary();
        } catch (ApiException ex) {
            throw new RuntimeException("Erro ao carregar resumo: " + ex.getMessage(), ex);
        }
    }

    public List<StudentDocumentResponse> listStudentDocuments(Integer studentId) {
        return boApi.listStudentDocuments(studentId);
    }

    public StudentDocumentResponse uploadStudentDocument(Integer studentId, Path path, String category) {
        return boApi.uploadStudentDocument(studentId, path, category);
    }

    public Path downloadStudentDocument(Integer studentId, Integer documentId, String fileName) {
        try {
            return boApi.downloadStudentDocument(studentId, documentId, fileName);
        } catch (Exception ex) {
            throw new RuntimeException("Erro no download: " + ex.getMessage(), ex);
        }
    }

    public void deleteStudentDocument(Integer studentId, Integer documentId) {
        boApi.deleteStudentDocument(studentId, documentId);
    }

    public List<Flight> allFlights() {
        return flightController.listarVoos();
    }

    public List<Payment> allPayments() {
        return paymentController.listarPagamentos();
    }

    public List<Evaluation> allEvaluations() {
        return evaluationController.listarAvaliacoes();
    }

    public List<Instructor> allInstructors() {
        return instructorController.listarInstrutores();
    }

    public long flightsToday() {
        LocalDate today = LocalDate.now();
        return allFlights().stream()
                .filter(f -> today.equals(f.getFlightDate()))
                .count();
    }

    public long completedFlightsToday() {
        LocalDate today = LocalDate.now();
        return allFlights().stream()
                .filter(f -> today.equals(f.getFlightDate()) && "completed".equals(f.getStatus()))
                .count();
    }

    public long scheduledFlightsToday() {
        LocalDate today = LocalDate.now();
        return allFlights().stream()
                .filter(f -> today.equals(f.getFlightDate()) && "scheduled".equals(f.getStatus()))
                .count();
    }

    public double flightHoursThisMonth() {
        YearMonth month = YearMonth.now();
        return allFlights().stream()
                .filter(f -> f.getFlightDate() != null && YearMonth.from(f.getFlightDate()).equals(month))
                .filter(f -> "completed".equals(f.getStatus()))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    public double revenueThisMonth() {
        YearMonth month = YearMonth.now();
        return allPayments().stream()
                .filter(p -> "paid".equals(p.getStatus()) && p.getPaidDate() != null)
                .filter(p -> YearMonth.from(p.getPaidDate()).equals(month))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
    }

    public double flightHoursInPeriod(LocalDate from, LocalDate to) {
        return allFlights().stream()
                .filter(f -> inPeriod(f.getFlightDate(), from, to))
                .filter(f -> "completed".equals(f.getStatus()))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    public double paidRevenueInPeriod(LocalDate from, LocalDate to) {
        return allPayments().stream()
                .filter(p -> "paid".equals(p.getStatus()) && p.getPaidDate() != null)
                .filter(p -> inPeriod(p.getPaidDate(), from, to))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
    }

    public int passRatePercent(LocalDate from, LocalDate to) {
        List<Evaluation> evals = allEvaluations().stream()
                .filter(e -> e.getEvaluationDate() != null && inPeriod(e.getEvaluationDate(), from, to))
                .filter(e -> "passed".equals(e.getStatus()) || "failed".equals(e.getStatus()))
                .toList();
        if (evals.isEmpty()) {
            return 0;
        }
        long passed = evals.stream().filter(e -> "passed".equals(e.getStatus())).count();
        return (int) Math.round((passed * 100.0) / evals.size());
    }

    public Map<String, Long> studentsByCourse(ReportsSummaryResponse summary) {
        if (summary != null && summary.studentsByCourse() != null && !summary.studentsByCourse().isEmpty()) {
            return summary.studentsByCourse();
        }
        return Map.of();
    }

    public Map<Integer, double[]> monthlyRevenueExpense(LocalDate from, LocalDate to) {
        Map<Integer, Double> revenue = new HashMap<>();
        Map<Integer, Double> expense = new HashMap<>();

        for (Payment p : allPayments()) {
            if (!"paid".equals(p.getStatus()) || p.getPaidDate() == null || !inPeriod(p.getPaidDate(), from, to)) {
                continue;
            }
            int month = p.getPaidDate().getMonthValue();
            revenue.merge(month, p.getAmount() != null ? p.getAmount() : 0.0, Double::sum);
        }

        for (Flight f : allFlights()) {
            if (!"completed".equals(f.getStatus()) || f.getFlightDate() == null || !inPeriod(f.getFlightDate(), from, to)) {
                continue;
            }
            int month = f.getFlightDate().getMonthValue();
            double cost = (f.getDuration() != null ? f.getDuration() : 0.0) * 120.0;
            expense.merge(month, cost, Double::sum);
        }

        Map<Integer, double[]> result = new LinkedHashMap<>();
        from.getMonthValue();
        int startMonth = from.getMonthValue();
        int endMonth = to.getMonthValue();
        for (int m = startMonth; m <= endMonth; m++) {
            result.put(m, new double[]{
                    revenue.getOrDefault(m, 0.0),
                    expense.getOrDefault(m, 0.0)
            });
        }
        if (result.isEmpty()) {
            for (int m = 1; m <= 7; m++) {
                result.put(m, new double[]{0.0, 0.0});
            }
        }
        return result;
    }

    public List<InstructorLoad> instructorLoads(LocalDate from, LocalDate to) {
        Map<Integer, InstructorLoad> loads = new LinkedHashMap<>();
        for (Instructor instructor : allInstructors()) {
            loads.put(instructor.getId(), new InstructorLoad(instructor.getName(), 0, 0));
        }
        for (Flight flight : allFlights()) {
            if (flight.getFlightDate() == null || !inPeriod(flight.getFlightDate(), from, to)) {
                continue;
            }
            if (flight.getInstructor() == null) {
                continue;
            }
            InstructorLoad load = loads.computeIfAbsent(
                    flight.getInstructor().getId(),
                    id -> new InstructorLoad(flight.getInstructor().getName(), 0, 0)
            );
            load.hours += flight.getDuration() != null ? (int) Math.round(flight.getDuration()) : 0;
            if (flight.getStudent() != null) {
                load.studentIds.add(flight.getStudent().getId());
            }
        }
        return loads.values().stream()
                .peek(l -> l.students = l.studentIds.size())
                .sorted(Comparator.comparingInt((InstructorLoad l) -> l.hours).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<ActivityItem> recentActivity(int limit) {
        List<ActivityItem> items = new ArrayList<>();

        allFlights().stream()
                .filter(f -> "completed".equals(f.getStatus()))
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .forEach(f -> items.add(new ActivityItem(
                        "✓",
                        "Voo " + (f.getAircraft() != null ? f.getAircraft().getRegistration() : "") + " concluido",
                        (f.getStudent() != null ? f.getStudent().getName() : "Aluno") + " · " + formatRelative(f.getFlightDate()),
                        "success"
                )));

        allPayments().stream()
                .filter(p -> "paid".equals(p.getStatus()))
                .sorted(Comparator.comparing(Payment::getPaidDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .forEach(p -> items.add(new ActivityItem(
                        "$",
                        "Pagamento recebido",
                        String.format("EUR %.0f · %s", p.getAmount() != null ? p.getAmount() : 0.0, formatRelative(p.getPaidDate())),
                        "success"
                )));

        return items.stream().limit(limit).toList();
    }

    public static LocalDate[] periodRange(String periodLabel) {
        LocalDate now = LocalDate.now();
        if ("2024".equals(periodLabel)) {
            return new LocalDate[]{LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)};
        }
        if (periodLabel != null && periodLabel.startsWith("Personalizado:")) {
            String[] parts = periodLabel.substring("Personalizado:".length()).split("\\|");
            if (parts.length == 2) {
                return new LocalDate[]{LocalDate.parse(parts[0]), LocalDate.parse(parts[1])};
            }
        }
        int year = now.getYear();
        return new LocalDate[]{LocalDate.of(year, 1, 1), LocalDate.of(year, 3, 31)};
    }

    private boolean inPeriod(LocalDate date, LocalDate from, LocalDate to) {
        return date != null && !date.isBefore(from) && !date.isAfter(to);
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

    public static class InstructorLoad {
        public final String name;
        public int hours;
        public int students;
        private final java.util.Set<Integer> studentIds = new java.util.HashSet<>();

        InstructorLoad(String name, int hours, int students) {
            this.name = name;
            this.hours = hours;
            this.students = students;
        }
    }

    public record ActivityItem(String icon, String title, String subtitle, String type) {
    }
}
