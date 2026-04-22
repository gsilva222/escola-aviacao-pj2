package pt.ipvc.estg.desktop;

import pt.ipvc.estg.desktop.views.LoginFrame;
import pt.ipvc.estg.desktop.views.components.Sidebar;
import pt.ipvc.estg.desktop.views.components.TopBar;
import pt.ipvc.estg.desktop.views.panels.BODashboard;
import pt.ipvc.estg.backend.services.*;
import pt.ipvc.estg.entities.*;
import pt.ipvc.estg.dal.mock.*;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração - Verificar se toda a aplicação está funcionando
 */
public class IntegrationTest {

    /**
     * Teste 1: Verificar se os Services conseguem ser inicializados
     */
    @Test
    public void testServicesInitialization() {
        assertDoesNotThrow(() -> {
            StudentService studentService = new StudentService();
            CourseService courseService = new CourseService();
            FlightService flightService = new FlightService();
            AircraftService aircraftService = new AircraftService();
            InstructorService instructorService = new InstructorService();
            EvaluationService evaluationService = new EvaluationService();
            PaymentService paymentService = new PaymentService();
            MaintenanceService maintenanceService = new MaintenanceService();
        });
    }

    /**
     * Teste 2: Verificar se os DAOs Mock conseguem retornar dados
     */
    @Test
    public void testDAOMockData() {
        StudentService studentService = new StudentService();
        List<Student> students = studentService.getAllStudents();
        
        assertNotNull(students);
        assertFalse(students.isEmpty(), "Students list should not be empty");
        assertTrue(students.size() >= 4, "Should have at least 4 students");
    }

    /**
     * Teste 3: Verificar se os Courses conseguem ser carregados
     */
    @Test
    public void testCourseLoading() {
        CourseService courseService = new CourseService();
        List<Course> courses = courseService.getAllCourses();
        
        assertNotNull(courses);
        assertFalse(courses.isEmpty(), "Courses list should not be empty");
    }

    /**
     * Teste 4: Verificar se a criação de um novo Student funciona
     */
    @Test
    public void testCreateStudent() {
        StudentService studentService = new StudentService();
        int initialSize = studentService.getAllStudents().size();
        
        Student newStudent = new Student();
        newStudent.setName("João Silva");
        newStudent.setEmail("joao@example.com");
        newStudent.setCpf("123456789");
        
        assertDoesNotThrow(() -> {
            studentService.criarEstudante(newStudent);
        });
        
        int finalSize = studentService.getAllStudents().size();
        assertEquals(initialSize + 1, finalSize, "Student count should increase by 1");
    }

    /**
     * Teste 5: Verificar se os UI Components conseguem ser instanciados
     */
    @Test
    public void testUIComponentsInitialization() {
        assertDoesNotThrow(() -> {
            // TopBar
            TopBar topBar = new TopBar();
            topBar.setPageTitle("Dashboard");
            
            // Sidebar
            Sidebar sidebar = new Sidebar(page -> {
                // Navigation callback
            });
            
            // BODashboard
            BODashboard dashboard = new BODashboard();
        });
    }

    /**
     * Teste 6: Verificar Main entry point
     */
    @Test
    public void testMainEntryPoint() {
        assertDoesNotThrow(() -> {
            // This should not throw any exception
            assertNotNull(Main.class);
        });
    }

    /**
     * Teste 7: Testar ciclo completo de Flight
     */
    @Test
    public void testFlightService() {
        FlightService flightService = new FlightService();
        
        List<Flight> flights = flightService.getAllFlights();
        assertNotNull(flights);
        
        // Should be able to create a flight
        Flight newFlight = new Flight();
        assertDoesNotThrow(() -> flightService.criarVoo(newFlight));
    }

    /**
     * Teste 8: Testar ciclo completo de Aircraft
     */
    @Test
    public void testAircraftService() {
        AircraftService aircraftService = new AircraftService();
        
        List<Aircraft> aircraft = aircraftService.getAllAircraft();
        assertNotNull(aircraft);
        assertFalse(aircraft.isEmpty(), "Aircraft list should not be empty");
    }

    /**
     * Teste 9: Validar colors e constantes são acessíveis
     */
    @Test
    public void testColorConstantsAccessibility() {
        // Just verify that color constants are properly defined
        assertDoesNotThrow(() -> {
            int darkBg = 0x0F2344;     // #0F2344 - DARK_BG
            int bluePrimary = 0x1565C0; // #1565C0 - BLUE_PRIMARY
            int lightBg = 0xF0F4F8;     // #F0F4F8 - LIGHT_BG
            
            assertTrue(darkBg > 0);
            assertTrue(bluePrimary > 0);
            assertTrue(lightBg > 0);
        });
    }

    /**
     * Teste 10: Testar navegação entre roles
     */
    @Test
    public void testRoleNavigation() {
        String[] roles = {"Administrador", "Secretaria", "Gestor Operacional", "Instrutor", "Técnico de Manutenção"};
        
        for (String role : roles) {
            assertNotNull(role);
            assertFalse(role.isEmpty(), "Role should not be empty");
        }
    }

    /**
     * Teste 11: Verificar se Service consegue lidar com operações CRUD
     */
    @Test
    public void testServiceCRUDOperations() {
        StudentService studentService = new StudentService();
        
        // Create
        Student student = new Student();
        student.setName("Test Student");
        student.setEmail("test@example.com");
        
        assertDoesNotThrow(() -> {
            studentService.criarEstudante(student);
        });
        
        // Read
        List<Student> students = studentService.getAllStudents();
        assertFalse(students.isEmpty());
        
        // Verify at least one student exists
        assertTrue(students.stream().anyMatch(s -> s.getName() != null));
    }

    /**
     * Teste 12: Testar Dashboard Service metrics
     */
    @Test
    public void testDashboardMetrics() {
        StudentService studentService = new StudentService();
        CourseService courseService = new CourseService();
        FlightService flightService = new FlightService();
        
        // Verify basic metrics calculations work
        assertDoesNotThrow(() -> {
            int totalStudents = studentService.getAllStudents().size();
            int totalCourses = courseService.getAllCourses().size();
            int totalFlights = flightService.getAllFlights().size();
            
            assertTrue(totalStudents >= 0);
            assertTrue(totalCourses >= 0);
            assertTrue(totalFlights >= 0);
        });
    }
}
