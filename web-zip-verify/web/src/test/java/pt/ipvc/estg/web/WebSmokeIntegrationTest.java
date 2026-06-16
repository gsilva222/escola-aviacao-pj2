package pt.ipvc.estg.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pt.ipvc.estg.web.dto.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "security.disable=false",
        "auth.allow-public-admin-register=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class WebSmokeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldExerciseNewFrontOfficeAndBackOfficeEndpoints() {
        String adminToken = registerAndLoginAdmin();
        Integer courseId = createCourse(adminToken);
        Integer instructorId = createInstructor(adminToken);
        Integer studentId = createStudent(adminToken, courseId, instructorId);

        AuthRegisterRequest registerStudent = new AuthRegisterRequest(
                "smoke.student", "student123", "STUDENT", studentId, null);
        ResponseEntity<AuthResponse> studentAuth = restTemplate.postForEntity(
                url("/auth/register"), registerStudent, AuthResponse.class);
        assertThat(studentAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        String studentToken = studentAuth.getBody().token();

        assertThat(get(url("/fo/me"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/dashboard"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/flights"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/schedule"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/hours"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/evaluations"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/payments"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/payments/summary"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/fo/documents"), studentToken).getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(get(url("/bo/reports/summary"), adminToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/bo/dashboard"), adminToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/bo/payments/summary"), adminToken).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get(url("/bo/payments/summary?studentId=" + studentId), adminToken).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(get(url("/bo/student-documents/" + studentId), adminToken).getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    private String registerAndLoginAdmin() {
        ResponseEntity<AuthResponse> register = restTemplate.postForEntity(
                url("/auth/register"),
                new AuthRegisterRequest("admin.smoke", "admin123", "ADMIN", null, "Administrador"),
                AuthResponse.class);
        assertThat(register.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CONFLICT);
        if (register.getStatusCode() == HttpStatus.OK && register.getBody() != null) {
            return register.getBody().token();
        }
        ResponseEntity<AuthResponse> login = restTemplate.postForEntity(
                url("/auth/login"), new AuthLoginRequest("admin.smoke", "admin123"), AuthResponse.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();
        return login.getBody().token();
    }

    private Integer createCourse(String token) {
        ResponseEntity<CourseResponse> r = restTemplate.exchange(
                url("/bo/courses"), HttpMethod.POST, withAuth(token,
                        new CourseRequest("SMK", "3m", 10, 15, 1000.0, "Smoke")),
                CourseResponse.class);
        return r.getBody().id();
    }

    private Integer createInstructor(String token) {
        ResponseEntity<InstructorResponse> r = restTemplate.exchange(
                url("/bo/instructors"), HttpMethod.POST, withAuth(token,
                        new InstructorRequest("Instrutor Smoke", "CPL", "PPL", 500, "active",
                                "inst@smoke.pt", "910000000")),
                InstructorResponse.class);
        return r.getBody().id();
    }

    private Integer createStudent(String token, Integer courseId, Integer instructorId) {
        ResponseEntity<StudentResponse> r = restTemplate.exchange(
                url("/bo/students"), HttpMethod.POST, withAuth(token,
                        new StudentRequest("Aluno Smoke", "smoke.student@email.com", "910000001",
                                "123456789", null, "Rua Smoke", "PT", courseId, instructorId,
                                "active", null, 5, 1.0, 0.5, "up_to_date")),
                StudentResponse.class);
        return r.getBody().id();
    }

    private ResponseEntity<String> get(String url, String token) {
        return restTemplate.exchange(url, HttpMethod.GET, withAuth(token, null), String.class);
    }

    private HttpEntity<Object> withAuth(String token, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private String url(String path) {
        return "http://localhost:" + port + "/api" + path;
    }
}
