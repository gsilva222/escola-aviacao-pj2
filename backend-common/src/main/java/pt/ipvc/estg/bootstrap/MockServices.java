package pt.ipvc.estg.bootstrap;

import pt.ipvc.estg.repositories.*;
import pt.ipvc.estg.repositories.mock.*;
import pt.ipvc.estg.services.*;

public final class MockServices {
    private static MockServices instance;

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final InstructorRepository instructorRepository;
    private final EvaluationRepository evaluationRepository;
    private final PaymentRepository paymentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final PerfilRepository perfilRepository;

    private final StudentService studentService;
    private final CourseService courseService;
    private final AircraftService aircraftService;
    private final FlightService flightService;
    private final InstructorService instructorService;
    private final EvaluationService evaluationService;
    private final PaymentService paymentService;
    private final MaintenanceService maintenanceService;
    private final PerfilService perfilService;
    private final PaymentSummaryService paymentSummaryService;
    private final ReportsService reportsService;

    private MockServices() {
        MockDataBootstrap.ensureSeeded();

        studentRepository = new MockStudentRepository();
        courseRepository = new MockCourseRepository();
        aircraftRepository = new MockAircraftRepository();
        flightRepository = new MockFlightRepository();
        instructorRepository = new MockInstructorRepository();
        evaluationRepository = new MockEvaluationRepository();
        paymentRepository = new MockPaymentRepository();
        maintenanceRepository = new MockMaintenanceRepository();
        perfilRepository = new MockPerfilRepository();

        studentService = new StudentService(studentRepository, courseRepository, instructorRepository);
        courseService = new CourseService(courseRepository);
        aircraftService = new AircraftService(aircraftRepository);
        flightService = new FlightService(flightRepository, studentRepository, instructorRepository, aircraftRepository, maintenanceRepository);
        instructorService = new InstructorService(instructorRepository);
        evaluationService = new EvaluationService(evaluationRepository, studentRepository, courseRepository);
        paymentService = new PaymentService(paymentRepository);
        maintenanceService = new MaintenanceService(maintenanceRepository, aircraftRepository, flightRepository);
        perfilService = new PerfilService(perfilRepository);
        paymentSummaryService = new PaymentSummaryService(paymentRepository);
        reportsService = new ReportsService(
                studentRepository, courseRepository, flightRepository,
                aircraftRepository, paymentRepository, maintenanceRepository,
                paymentSummaryService
        );
    }

    public static synchronized MockServices getInstance() {
        if (instance == null) {
            instance = new MockServices();
        }
        return instance;
    }

    public StudentService studentService() { return studentService; }
    public CourseService courseService() { return courseService; }
    public AircraftService aircraftService() { return aircraftService; }
    public FlightService flightService() { return flightService; }
    public InstructorService instructorService() { return instructorService; }
    public EvaluationService evaluationService() { return evaluationService; }
    public PaymentService paymentService() { return paymentService; }
    public MaintenanceService maintenanceService() { return maintenanceService; }
    public PerfilService perfilService() { return perfilService; }
    public PaymentSummaryService paymentSummaryService() { return paymentSummaryService; }
    public ReportsService reportsService() { return reportsService; }

    public StudentRepository studentRepository() { return studentRepository; }
    public CourseRepository courseRepository() { return courseRepository; }
    public AircraftRepository aircraftRepository() { return aircraftRepository; }
    public FlightRepository flightRepository() { return flightRepository; }
    public InstructorRepository instructorRepository() { return instructorRepository; }
    public EvaluationRepository evaluationRepository() { return evaluationRepository; }
    public PaymentRepository paymentRepository() { return paymentRepository; }
    public MaintenanceRepository maintenanceRepository() { return maintenanceRepository; }
    public PerfilRepository perfilRepository() { return perfilRepository; }
}
