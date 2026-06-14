package pt.ipvc.estg.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import pt.ipvc.estg.web.dto.*;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EvaluationCourseMismatchIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRejectCreatingEvaluationWhenStudentCourseDoesNotMatch() {
        Integer courseA = createCourse("COURSE_A");
        Integer courseB = createCourse("COURSE_B");
        Integer studentId = createStudent(courseA, "t1");

        EvaluationRequest req = new EvaluationRequest(
                studentId,
                courseB,
                "Exam mismatch",
                LocalDate.now(),
                null,
                100,
                "scheduled",
                "theoretical",
                null
        );

        ResponseEntity<String> resp = restTemplate.exchange(
                url("/bo/evaluations"),
                HttpMethod.POST,
                json(req),
                String.class
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).contains("Aluno nao esta neste curso");
    }

    @Test
    void shouldRejectUpdatingEvaluationCourseWhenStudentCourseDoesNotMatch() {
        Integer courseA = createCourse("COURSE_A2");
        Integer courseB = createCourse("COURSE_B2");
        Integer studentId = createStudent(courseA, "t2");

        // cria com course correto
        EvaluationRequest createReq = new EvaluationRequest(
                studentId,
                courseA,
                "Exam ok",
                LocalDate.now(),
                null,
                100,
                "scheduled",
                "theoretical",
                null
        );

        ResponseEntity<EvaluationResponse> created = restTemplate.exchange(
                url("/bo/evaluations"),
                HttpMethod.POST,
                json(createReq),
                EvaluationResponse.class
        );
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        Integer evaluationId = created.getBody().id();

        // tenta mudar para course errado via PUT
        EvaluationRequest updateReq = new EvaluationRequest(
                studentId,
                courseB,
                "Exam wrong",
                LocalDate.now(),
                null,
                100,
                "scheduled",
                "theoretical",
                null
        );

        ResponseEntity<String> resp = restTemplate.exchange(
                url("/bo/evaluations/" + evaluationId),
                HttpMethod.PUT,
                json(updateReq),
                String.class
        );

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).contains("Aluno nao esta neste curso");
    }

    private Integer createCourse(String name) {
        CourseRequest course = new CourseRequest(
                name,
                "12 meses",
                45,
                40,
                8500.0,
                "Curso " + name
        );

        ResponseEntity<CourseResponse> response = restTemplate.postForEntity(
                url("/bo/courses"),
                course,
                CourseResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().id();
    }

    private Integer createStudent(Integer courseId, String testSuffix) {
        String suffix = testSuffix + "_" + System.nanoTime();
        String email = "aluno.mismatch." + suffix + "@email.com";
        // NIF deve ter 9 dígitos. Geramos determinísticamente a partir do suffix.
        String nif = String.valueOf(Math.abs(suffix.hashCode()));
        nif = (nif + "000000000").substring(0, 9);
        StudentRequest student = new StudentRequest(
                "Aluno Course Mismatch",
                email,
                nif,
                null,
                null,
                "Rua Teste",
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

        ResponseEntity<StudentResponse> response = restTemplate.postForEntity(
                url("/bo/students"),
                student,
                StudentResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().id();
    }

    private HttpEntity<Object> json(Object body) {
        return new HttpEntity<>(body, jsonHeaders());
    }

    private org.springframework.http.HttpHeaders jsonHeaders() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String url(String path) {
        return "http://localhost:" + port + "/api" + path;
    }
}

