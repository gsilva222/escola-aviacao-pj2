package pt.ipvc.estg.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.dal.mock.MockDalReset;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.*;
import pt.ipvc.estg.repositories.mock.*;

import static org.junit.jupiter.api.Assertions.*;

class ReportsServiceTest {

    private ReportsService reportsService;
    private Student student;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        StudentRepository studentRepository = new MockStudentRepository();
        CourseRepository courseRepository = new MockCourseRepository();
        FlightRepository flightRepository = new MockFlightRepository();
        AircraftRepository aircraftRepository = new MockAircraftRepository();
        PaymentRepository paymentRepository = new MockPaymentRepository();
        MaintenanceRepository maintenanceRepository = new MockMaintenanceRepository();
        PaymentSummaryService paymentSummaryService = new PaymentSummaryService(paymentRepository);

        reportsService = new ReportsService(
                studentRepository, courseRepository, flightRepository,
                aircraftRepository, paymentRepository, maintenanceRepository,
                paymentSummaryService
        );

        Course course = courseRepository.save(new Course("PPL", "12 meses", 45, 100, 25000.0));
        student = studentRepository.save(new Student("Test", "test@test.com", course));
        student.setStatus("active");
        studentRepository.save(student);

        aircraftRepository.save(new Aircraft("CS-ABC", "C172", "single_engine"));
        Payment pending = new Payment(student, "Propina", 100.0, null);
        pending.setStatus("pending");
        paymentRepository.save(pending);
    }

    @Test
    void buildSummary_aggregatesCounts() {
        var summary = reportsService.buildSummary();
        assertEquals(1, summary.totalStudents());
        assertEquals(1, summary.activeStudents());
        assertEquals(1, summary.totalCourses());
        assertEquals(1, summary.totalAircraft());
        assertEquals(1, summary.pendingPayments());
        assertTrue(summary.totalPendingAmount() >= 100.0);
    }
}
