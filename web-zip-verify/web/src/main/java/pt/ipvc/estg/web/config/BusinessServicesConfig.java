package pt.ipvc.estg.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.ipvc.estg.repositories.*;
import pt.ipvc.estg.services.*;
import pt.ipvc.estg.web.services.StudentScopeService;

@Configuration
public class BusinessServicesConfig {

    @Bean
    public StudentService studentService(StudentRepository studentRepository,
                                         CourseRepository courseRepository,
                                         InstructorRepository instructorRepository) {
        return new StudentService(studentRepository, courseRepository, instructorRepository);
    }

    @Bean
    public CourseService courseService(CourseRepository courseRepository) {
        return new CourseService(courseRepository);
    }

    @Bean
    public AircraftService aircraftService(AircraftRepository aircraftRepository) {
        return new AircraftService(aircraftRepository);
    }

    @Bean
    public FlightService flightService(FlightRepository flightRepository,
                                       StudentRepository studentRepository,
                                       InstructorRepository instructorRepository,
                                       AircraftRepository aircraftRepository,
                                       MaintenanceRepository maintenanceRepository) {
        return new FlightService(flightRepository, studentRepository, instructorRepository, aircraftRepository, maintenanceRepository);
    }

    @Bean
    public InstructorService instructorService(InstructorRepository instructorRepository) {
        return new InstructorService(instructorRepository);
    }

    @Bean
    public EvaluationService evaluationService(EvaluationRepository evaluationRepository,
                                               StudentRepository studentRepository,
                                               CourseRepository courseRepository) {
        return new EvaluationService(evaluationRepository, studentRepository, courseRepository);
    }

    @Bean
    public PaymentService paymentService(PaymentRepository paymentRepository) {
        return new PaymentService(paymentRepository);
    }

    @Bean
    public MaintenanceService maintenanceService(MaintenanceRepository maintenanceRepository,
                                                 AircraftRepository aircraftRepository,
                                                 FlightRepository flightRepository) {
        return new MaintenanceService(maintenanceRepository, aircraftRepository, flightRepository);
    }

    @Bean
    public PaymentSummaryService paymentSummaryService(PaymentRepository paymentRepository) {
        return new PaymentSummaryService(paymentRepository);
    }

    @Bean
    public ReportsService reportsService(StudentRepository studentRepository,
                                         CourseRepository courseRepository,
                                         FlightRepository flightRepository,
                                         AircraftRepository aircraftRepository,
                                         PaymentRepository paymentRepository,
                                         MaintenanceRepository maintenanceRepository,
                                         PaymentSummaryService paymentSummaryService) {
        return new ReportsService(studentRepository, courseRepository, flightRepository,
                aircraftRepository, paymentRepository, maintenanceRepository, paymentSummaryService);
    }

    @Bean
    public StudentDocumentService studentDocumentService(StudentDocumentRepository documentRepository) {
        return new StudentDocumentService(documentRepository);
    }

    @Bean
    public AccountService accountService(UserAccountRepository userAccountRepository,
                                         StudentRepository studentRepository) {
        return new AccountService(userAccountRepository, studentRepository);
    }

    @Bean
    public FoDashboardService foDashboardService(StudentScopeService studentScopeService,
                                                 FlightRepository flightRepository,
                                                 EvaluationRepository evaluationRepository,
                                                 PaymentSummaryService paymentSummaryService) {
        return new FoDashboardService(
                studentScopeService::requireCurrentStudent,
                flightRepository,
                evaluationRepository,
                paymentSummaryService
        );
    }

    @Bean
    public BoDashboardService boDashboardService(ReportsService reportsService,
                                                 FlightRepository flightRepository,
                                                 PaymentRepository paymentRepository) {
        return new BoDashboardService(reportsService, flightRepository, paymentRepository);
    }

    @Bean
    public PerfilService perfilService(PerfilRepository perfilRepository) {
        return new PerfilService(perfilRepository);
    }
}
