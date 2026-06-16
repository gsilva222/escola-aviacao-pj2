package pt.ipvc.estg.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pt.ipvc.estg.web.dto.AuthLoginRequest;
import pt.ipvc.estg.web.dto.AuthRegisterRequest;
import pt.ipvc.estg.web.dto.AuthResponse;
import pt.ipvc.estg.web.dto.CourseRequest;
import pt.ipvc.estg.web.dto.CourseResponse;
import pt.ipvc.estg.web.dto.StudentRequest;
import pt.ipvc.estg.web.dto.StudentResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "security.disable=false",
        "auth.allow-public-admin-register=true"
})
class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRejectBackOfficeWithoutToken() {
        ResponseEntity<String> response = restTemplate.getForEntity(url("/bo/courses"), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldRejectStudentTokenOnBackOfficeEndpoint() {
        String adminToken = registerAndLoginAdmin();
        Integer courseId = createCourse(adminToken);
        Integer studentId = createStudent(adminToken, courseId);

        AuthRegisterRequest registerStudent = new AuthRegisterRequest(
                "student.security",
                "student123",
                "STUDENT",
                studentId,
                null
        );
        ResponseEntity<AuthResponse> studentRegisterResponse = restTemplate.postForEntity(
                url("/auth/register"),
                registerStudent,
                AuthResponse.class
        );

        assertThat(studentRegisterResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(studentRegisterResponse.getBody()).isNotNull();

        ResponseEntity<String> boResponse = restTemplate.exchange(
                url("/bo/courses"),
                HttpMethod.GET,
                withAuth(studentRegisterResponse.getBody().token(), null),
                String.class
        );

        assertThat(boResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String registerAndLoginAdmin() {
        AuthRegisterRequest register = new AuthRegisterRequest(
                "admin.security",
                "admin123",
                "ADMIN",
                null,
                null
        );
        restTemplate.postForEntity(url("/auth/register"), register, AuthResponse.class);

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                url("/auth/login"),
                new AuthLoginRequest("admin.security", "admin123"),
                AuthResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        return loginResponse.getBody().token();
    }

    private Integer createCourse(String token) {
        CourseRequest course = new CourseRequest(
                "SEC",
                "3 meses",
                10,
                15,
                1200.0,
                "Curso de seguranca"
        );
        ResponseEntity<CourseResponse> response = restTemplate.exchange(
                url("/bo/courses"),
                HttpMethod.POST,
                withAuth(token, course),
                CourseResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response.getBody().id();
    }

    private Integer createStudent(String token, Integer courseId) {
        StudentRequest student = new StudentRequest(
                "Aluno Security",
                "student.security@email.com",
                "912000111",
                null,
                null,
                "Rua Security",
                "Portugal",
                courseId,
                null,
                "active",
                null,
                0,
                0.0,
                0.0,
                "up_to_date"
        );
        ResponseEntity<StudentResponse> response = restTemplate.exchange(
                url("/bo/students"),
                HttpMethod.POST,
                withAuth(token, student),
                StudentResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
