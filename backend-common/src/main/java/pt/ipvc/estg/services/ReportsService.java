package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.ReportsSummary;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.AircraftRepository;
import pt.ipvc.estg.repositories.CourseRepository;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.repositories.MaintenanceRepository;
import pt.ipvc.estg.repositories.PaymentRepository;
import pt.ipvc.estg.repositories.StudentRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportsService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FlightRepository flightRepository;
    private final AircraftRepository aircraftRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PaymentSummaryService paymentSummaryService;

    public ReportsService(StudentRepository studentRepository,
                          CourseRepository courseRepository,
                          FlightRepository flightRepository,
                          AircraftRepository aircraftRepository,
                          PaymentRepository paymentRepository,
                          MaintenanceRepository maintenanceRepository,
                          PaymentSummaryService paymentSummaryService) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.flightRepository = flightRepository;
        this.aircraftRepository = aircraftRepository;
        this.paymentRepository = paymentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.paymentSummaryService = paymentSummaryService;
    }

    public ReportsService() {
        this(
                pt.ipvc.estg.bootstrap.MockServices.getInstance().studentRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().courseRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().flightRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().aircraftRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().paymentRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().maintenanceRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().paymentSummaryService()
        );
    }

    public ReportsSummary buildSummary() {
        List<Student> students = studentRepository.findAll();
        long active = students.stream().filter(s -> "active".equals(s.getStatus())).count();
        long suspended = students.stream().filter(s -> "suspended".equals(s.getStatus())).count();
        long completed = students.stream().filter(s -> "completed".equals(s.getStatus())).count();

        Map<String, Long> studentsByCourse = new LinkedHashMap<>();
        for (Student student : students) {
            if (student.getCourse() == null) {
                continue;
            }
            String name = student.getCourse().getName();
            studentsByCourse.merge(name, 1L, Long::sum);
        }

        List<Aircraft> aircraft = aircraftRepository.findAll();
        long operational = aircraft.stream().filter(a -> "operational".equals(a.getStatus())).count();
        long maintenance = aircraft.stream().filter(a -> "maintenance".equals(a.getStatus())).count();
        long grounded = aircraft.stream().filter(a -> "grounded".equals(a.getStatus())).count();

        List<Payment> payments = paymentRepository.findAll();
        long pendingPayments = payments.stream().filter(p -> "pending".equals(p.getStatus())).count();
        long overduePayments = payments.stream().filter(p -> "overdue".equals(p.getStatus())).count();

        long activeMaintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getStatus() != null && !"completed".equals(m.getStatus()))
                .count();

        var paymentSummary = paymentSummaryService.summarizeAll();

        return new ReportsSummary(
                students.size(),
                active,
                suspended,
                completed,
                courseRepository.count(),
                flightRepository.count(),
                flightRepository.countByStatus("scheduled"),
                flightRepository.countByStatus("completed"),
                aircraft.size(),
                operational,
                maintenance,
                grounded,
                pendingPayments,
                overduePayments,
                paymentSummary.totalPending(),
                activeMaintenances,
                studentsByCourse
        );
    }
}
