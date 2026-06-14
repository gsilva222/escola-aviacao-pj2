package pt.ipvc.estg.desktop.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.desktop.api.dto.FoDashboardResponse;
import pt.ipvc.estg.desktop.api.dto.ReportsSummaryResponse;
import pt.ipvc.estg.desktop.api.fo.FoApiService;
import pt.ipvc.estg.desktop.services.DesktopAuthService;
import pt.ipvc.estg.web.WebApp;
import pt.ipvc.estg.web.dto.AuthLoginRequest;
import pt.ipvc.estg.web.dto.AuthRegisterRequest;
import pt.ipvc.estg.web.dto.AuthResponse;
import pt.ipvc.estg.web.dto.CourseRequest;
import pt.ipvc.estg.web.dto.CourseResponse;
import pt.ipvc.estg.web.dto.InstructorRequest;
import pt.ipvc.estg.web.dto.InstructorResponse;
import pt.ipvc.estg.web.dto.StudentRequest;
import pt.ipvc.estg.web.dto.StudentResponse;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = WebApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = { "security.disable=false", "auth.allow-public-admin-register=true" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DesktopApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void configureApiClient() {
        System.setProperty("aeroschool.api.baseUrl", "http://localhost:" + port + "/api");
        System.setProperty("aeroschool.api.enabled", "true");
        SessionContext.clear();
    }

    @AfterEach
    void cleanup() {
        SessionContext.clear();
        System.clearProperty("aeroschool.api.baseUrl");
        System.clearProperty("aeroschool.api.enabled");
    }

    @Test
    void shouldLoginAdminAndLoadReportsSummary() {
        DesktopAuthService authService = new DesktopAuthService();
        registerAdmin("desktop.admin", "admin123");
        authService.loginBackOffice("desktop.admin", "admin123", "Administrador");

        assertTrue(BoDataAccess.useApi());
        assertEquals("desktop.admin", SessionContext.getUsername());

        ReportsSummaryResponse summary = new BoApiService().getReportsSummary();
        assertNotNull(summary);
        assertTrue(summary.totalStudents() >= 0);

        DesktopAuthService.logout();
        assertFalse(SessionContext.isAuthenticated());
        assertFalse(BoDataAccess.useApi());
    }

    @Test
    void shouldLoginStudentAndLoadFrontOfficeData() {
        String adminToken = registerAdmin("desktop.admin2", "admin123");
        Integer studentId = seedStudent(adminToken);

        ResponseEntity<AuthResponse> studentAuth = restTemplate.postForEntity(
                apiUrl("/auth/register"),
                new AuthRegisterRequest("desktop.student", "student123", "STUDENT", studentId, null),
                AuthResponse.class);
        assertEquals(HttpStatus.OK, studentAuth.getStatusCode());

        DesktopAuthService authService = new DesktopAuthService();
        authService.loginStudent("desktop.student", "student123");

        assertTrue(FoDataAccess.useApi());
        assertEquals(studentId, SessionContext.getStudentId());

        FoDashboardResponse dashboard = new FoApiService().getDashboard();
        assertNotNull(dashboard);

        DesktopAuthService.logout();
        assertFalse(FoDataAccess.useApi());
    }

    private String registerAdmin(String username, String password) {
        ResponseEntity<AuthResponse> register = restTemplate.postForEntity(
                apiUrl("/auth/register"),
                new AuthRegisterRequest(username, password, "ADMIN", null, "Administrador"),
                AuthResponse.class);
        assertTrue(register.getStatusCode() == HttpStatus.OK || register.getStatusCode() == HttpStatus.CONFLICT);

        ResponseEntity<AuthResponse> login = restTemplate.postForEntity(
                apiUrl("/auth/login"),
                new AuthLoginRequest(username, password),
                AuthResponse.class);
        assertEquals(HttpStatus.OK, login.getStatusCode());
        assertNotNull(login.getBody());
        return login.getBody().token();
    }

    private Integer seedStudent(String adminToken) {
        Integer courseId = createCourse(adminToken);
        createInstructor(adminToken);
        return createStudent(adminToken, courseId);
    }

    private Integer createCourse(String token) {
        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                apiUrl("/bo/courses"),
                HttpMethod.POST,
                withAuth(token, new CourseRequest("PPL", "6m", 45, 100, 12000.0, "Desktop test")),
                CourseResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        return response.getBody().id();
    }

    private void createInstructor(String token) {
        ResponseEntity<InstructorResponse> response = restTemplate.exchange(
                apiUrl("/bo/instructors"),
                HttpMethod.POST,
                withAuth(token, new InstructorRequest("Capt. Desktop", "FI-001", "PPL", 100, "active", "c@aero.pt", "910000000")),
                InstructorResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private Integer createStudent(String token, Integer courseId) {
        StudentRequest request = new StudentRequest(
                "Aluno Desktop", "desktop.student@email.com", "910000001", "123456789",
                LocalDate.of(2000, 1, 1), "Rua Teste", "Portugal", courseId, null,
                "active", LocalDate.now(), 0, 0.0, 0.0, "pending"
        );
        ResponseEntity<StudentResponse> response = restTemplate.exchange(
                apiUrl("/bo/students"),
                HttpMethod.POST,
                withAuth(token, request),
                StudentResponse.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        return response.getBody().id();
    }

    private HttpEntity<Object> withAuth(String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    private String apiUrl(String path) {
        return "http://localhost:" + port + "/api" + path;
    }
}
