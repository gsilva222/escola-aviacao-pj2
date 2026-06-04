package pt.ipvc.estg.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pt.ipvc.estg.entities.*;
import pt.ipvc.estg.web.repositories.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Order(1)
public class BusinessDataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final AircraftRepository aircraftRepository;
    private final StudentRepository studentRepository;
    private final FlightRepository flightRepository;
    private final EvaluationRepository evaluationRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final boolean seedEnabled;
    private final boolean businessSeedEnabled;

    public BusinessDataSeeder(CourseRepository courseRepository,
                              InstructorRepository instructorRepository,
                              AircraftRepository aircraftRepository,
                              StudentRepository studentRepository,
                              FlightRepository flightRepository,
                              EvaluationRepository evaluationRepository,
                              PaymentRepository paymentRepository,
                              MaintenanceRepository maintenanceRepository,
                              @Value("${seed.enabled:true}") boolean seedEnabled,
                              @Value("${seed.business.enabled:true}") boolean businessSeedEnabled) {
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.aircraftRepository = aircraftRepository;
        this.studentRepository = studentRepository;
        this.flightRepository = flightRepository;
        this.evaluationRepository = evaluationRepository;
        this.paymentRepository = paymentRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.seedEnabled = seedEnabled;
        this.businessSeedEnabled = businessSeedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || !businessSeedEnabled || courseRepository.count() > 0) {
            return;
        }
        seedReferenceData();
    }

    private void seedReferenceData() {
        Course ppl = saveCourse("PPL – Piloto Privado", "12 meses", 45, 100, 8500.0);
        Course cpl = saveCourse("CPL – Piloto Comercial", "18 meses", 200, 250, 28000.0);
        Course ir = saveCourse("IR – Habilitação por Instrumentos", "6 meses", 50, 150, 12000.0);

        Instructor i1 = saveInstructor("Capt. António Ferreira", "CPL/IR/FI", "PPL, IR");
        Instructor i2 = saveInstructor("Capt. Margarida Lopes", "CPL/IR/FI/IRI", "PPL, CPL");
        Instructor i3 = saveInstructor("Capt. José Pereira", "ATPL/FI/TRI", "CPL, ATPL");

        Aircraft a1 = saveAircraft("CS-AER", "Cessna 172 Skyhawk", "Single Engine", 2018, "operational");
        Aircraft a2 = saveAircraft("CS-FLY", "Piper PA-28 Cherokee", "Single Engine", 2016, "operational");
        Aircraft a3 = saveAircraft("CS-NAV", "Cessna 172 Skyhawk", "Single Engine", 2020, "maintenance");

        Student s1 = saveStudent("João Silva", "joao.silva@email.com", ppl, i1, "912000001", "100000001");
        Student s2 = saveStudent("Maria Santos", "maria.santos@email.com", cpl, i2, "912000002", "100000002");
        Student s3 = saveStudent("Pedro Oliveira", "pedro.oliveira@email.com", ir, i3, "912000003", "100000003");
        Student s4 = saveStudent("Ana Ferreira", "ana.ferreira@email.com", ppl, i1, "912000004", "100000004");

        seedFlights(s1, i1, a1);
        seedFlights(s2, i2, a2);
        seedFlights(s3, i3, a1);
        seedFlights(s4, i1, a2);

        seedEvaluations(s1, ppl);
        seedEvaluations(s2, cpl);
        seedPayments(s1);
        seedPayments(s2);
        seedPayments(s3);
        seedMaintenance(a3);
    }

    private Course saveCourse(String name, String duration, int flightHours, int theoreticalHours, double price) {
        Course course = new Course(name, duration, flightHours, theoreticalHours, price);
        course.setDescription("Curso " + name);
        return courseRepository.save(course);
    }

    private Instructor saveInstructor(String name, String license, String specialization) {
        Instructor instructor = new Instructor(name, license, specialization);
        instructor.setStatus("active");
        instructor.setFlightHours(1200);
        instructor.setEmail(name.toLowerCase().replace(' ', '.') + "@aeroschool.pt");
        instructor.setPhone("913000000");
        return instructorRepository.save(instructor);
    }

    private Aircraft saveAircraft(String registration, String model, String type, int year, String status) {
        Aircraft aircraft = new Aircraft(registration, model, type);
        aircraft.setManufYear(year);
        aircraft.setStatus(status);
        aircraft.setFlightHours(1200.0);
        aircraft.setFuelLevel(80);
        aircraft.setLocation("Hangar A");
        aircraft.setLastMaintenance(LocalDate.now().minusMonths(2));
        aircraft.setNextMaintenance(LocalDate.now().plusMonths(4));
        return aircraftRepository.save(aircraft);
    }

    private Student saveStudent(String name, String email, Course course, Instructor instructor,
                                String phone, String nif) {
        Student student = new Student(name, email, course);
        student.setInstructor(instructor);
        student.setPhone(phone);
        student.setNif(nif);
        student.setBirthdate(LocalDate.of(1998, 5, 12));
        student.setAddress("Rua da Aviacao, 100, Porto");
        student.setNationality("Portuguesa");
        student.setStatus("active");
        student.setEnrollmentDate(LocalDate.now().minusMonths(6));
        student.setProgress(35);
        student.setFlightHours(12.5);
        student.setTheoreticalHours(28.0);
        student.setPaymentStatus("up_to_date");
        student.setAvatar(generateAvatar(name));
        return studentRepository.save(student);
    }

    private void seedFlights(Student student, Instructor instructor, Aircraft aircraft) {
        flightRepository.save(buildFlight(student, instructor, aircraft, LocalDate.now().minusDays(14),
                LocalTime.of(9, 0), 1.5, "LPPT", "LPPT", "Local", "completed"));
        flightRepository.save(buildFlight(student, instructor, aircraft, LocalDate.now().minusDays(7),
                LocalTime.of(11, 30), 2.0, "LPPT", "LPCS", "Navigation", "completed"));
        flightRepository.save(buildFlight(student, instructor, aircraft, LocalDate.now().plusDays(3),
                LocalTime.of(10, 0), 1.5, "LPPT", "LPPT", "Local", "scheduled"));
    }

    private Flight buildFlight(Student student, Instructor instructor, Aircraft aircraft,
                             LocalDate date, LocalTime time, double duration,
                             String origin, String destination, String type, String status) {
        Flight flight = new Flight();
        flight.setStudent(student);
        flight.setInstructor(instructor);
        flight.setAircraft(aircraft);
        flight.setFlightDate(date);
        flight.setFlightTime(time);
        flight.setDuration(duration);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setFlightType(type);
        flight.setStatus(status);
        flight.setObjectives("Treino regulamentar");
        return flight;
    }

    private void seedEvaluations(Student student, Course course) {
        Evaluation passed = new Evaluation();
        passed.setStudent(student);
        passed.setCourse(course);
        passed.setExamName("Teorico modulo 1");
        passed.setEvaluationDate(LocalDate.now().minusMonths(2));
        passed.setScore(82);
        passed.setMaxScore(100);
        passed.setStatus("passed");
        passed.setEvaluationType("theoretical");
        evaluationRepository.save(passed);

        Evaluation scheduled = new Evaluation();
        scheduled.setStudent(student);
        scheduled.setCourse(course);
        scheduled.setExamName("Pratico modulo 2");
        scheduled.setEvaluationDate(LocalDate.now().plusDays(20));
        scheduled.setScore(null);
        scheduled.setMaxScore(100);
        scheduled.setStatus("scheduled");
        scheduled.setEvaluationType("practical");
        evaluationRepository.save(scheduled);
    }

    private void seedPayments(Student student) {
        paymentRepository.save(buildPayment(student, "Propina trimestre", 1200.0, LocalDate.now().plusDays(15), "pending"));
        paymentRepository.save(buildPayment(student, "Taxa de exame", 250.0, LocalDate.now().minusDays(30), "paid", LocalDate.now().minusDays(28)));
    }

    private Payment buildPayment(Student student, String description, double amount, LocalDate dueDate, String status) {
        return buildPayment(student, description, amount, dueDate, status, null);
    }

    private Payment buildPayment(Student student, String description, double amount,
                                 LocalDate dueDate, String status, LocalDate paidDate) {
        Payment payment = new Payment(student, description, amount, dueDate);
        payment.setStatus(status);
        payment.setPaymentMethod("Transferencia");
        if (paidDate != null) {
            payment.setPaidDate(paidDate);
        }
        return payment;
    }

    private void seedMaintenance(Aircraft aircraft) {
        Maintenance maintenance = new Maintenance();
        maintenance.setAircraft(aircraft);
        maintenance.setMaintenanceType("Inspection");
        maintenance.setDescription("Revisao programada");
        maintenance.setTechnician("Tec. Carlos");
        maintenance.setStartDate(LocalDate.now().minusDays(5));
        maintenance.setEstimatedEndDate(LocalDate.now().plusDays(10));
        maintenance.setStatus("in_progress");
        maintenance.setPriority("medium");
        maintenance.setCost(1500.0);
        maintenanceRepository.save(maintenance);
    }

    private String generateAvatar(String name) {
        String[] parts = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.charAt(0));
                if (sb.length() == 2) {
                    break;
                }
            }
        }
        return sb.toString().toUpperCase();
    }
}
