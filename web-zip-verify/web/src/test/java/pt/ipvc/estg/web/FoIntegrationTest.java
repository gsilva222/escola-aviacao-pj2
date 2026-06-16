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
class FoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAccessFrontOfficeAsStudent() {
        String adminToken = registerAndLoginAdmin();
        Integer courseId = createCourse(adminToken);
        Integer studentId = createStudent(adminToken, courseId);

        AuthRegisterRequest registerStudent = new AuthRegisterRequest(
                "aluno.fo",
                "aluno123",
                "STUDENT",
                studentId,
                null
        );
        ResponseEntity<AuthResponse> studentAuth = restTemplate.postForEntity(
                url("/auth/register"),
                registerStudent,
                AuthResponse.class
        );
        assertThat(studentAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentAuth.getBody()).isNotNull();
        String studentToken = studentAuth.getBody().token();

        ResponseEntity<FoDashboardResponse> dashboard = restTemplate.exchange(
                url("/fo/dashboard"),
                HttpMethod.GET,
                withAuth(studentToken, null),
                FoDashboardResponse.class
        );
        assertThat(dashboard.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(dashboard.getBody()).isNotNull();
        assertThat(dashboard.getBody().studentId()).isEqualTo(studentId);

        ResponseEntity<String> flights = restTemplate.exchange(
                url("/fo/flights"),
                HttpMethod.GET,
                withAuth(studentToken, null),
                String.class
        );
        assertThat(flights.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> forbiddenBo = restTemplate.exchange(
                url("/bo/courses"),
                HttpMethod.GET,
                withAuth(studentToken, null),
                String.class
        );
        assertThat(forbiddenBo.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String registerAndLoginAdmin() {
        AuthRegisterRequest register = new AuthRegisterRequest("admin.fo", "admin123", "ADMIN", null, null);
        restTemplate.postForEntity(url("/auth/register"), register, AuthResponse.class);
        ResponseEntity<AuthResponse> login = restTemplate.postForEntity(
                url("/auth/login"),
                new AuthLoginRequest("admin.fo", "admin123"),
                AuthResponse.class
        );
        assertThat(login.getBody()).isNotNull();
        return login.getBody().token();
    }

    private Integer createCourse(String token) {
        CourseRequest course = new CourseRequest("FO", "3 meses", 10, 15, 1200.0, "Curso FO");
        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                url("/bo/courses"),
                HttpMethod.POST,
                withAuth(token, course),
                CourseResponse.class
        );
        assertThat(response.getBody()).isNotNull();
        return response.getBody().id();
    }

    private Integer createStudent(String token, Integer courseId) {
        StudentRequest student = new StudentRequest(
                "Aluno FO",
                "aluno.fo.test@email.com",
                "912000999",
                "123456789",
                null,
                "Rua FO",
                "Portugal",
                courseId,
                null,
                "active",
                null,
                10,
                1.0,
                0.5,
                "up_to_date"
        );
        ResponseEntity<StudentResponse> response = restTemplate.exchange(
                url("/bo/students"),
                HttpMethod.POST,
                withAuth(token, student),
                StudentResponse.class
        );
        assertThat(response.getBody()).isNotNull();
        return response.getBody().id();
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
