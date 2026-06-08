package pt.ipvc.estg.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import pt.ipvc.estg.web.dto.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

        @Autowired
        private ObjectMapper objectMapper;

    @Test
    void shouldRegisterAndLoginAdmin() {
        ResponseEntity<AuthResponse> registerResponse = registerAdmin();
        assertThat(registerResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.CONFLICT);
        if (registerResponse.getStatusCode() == HttpStatus.OK) {
            assertThat(registerResponse.getBody()).isNotNull();
            assertThat(registerResponse.getBody().token()).isNotBlank();
        }

        AuthLoginRequest login = new AuthLoginRequest("admin", "admin123");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                url("/auth/login"),
                login,
                AuthResponse.class
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().token()).isNotBlank();
    }

    @Test
    void shouldCreateCourseAndStudent() {
        String token = registerAndLoginAdmin();

        Integer courseId = createCourse(token, "PPL");
        Integer studentId = createStudent(token, courseId, "joao.silva@email.com");

        ResponseEntity<String> getStudent = restTemplate.exchange(
                url("/bo/students/" + studentId),
                HttpMethod.GET,
                withAuth(token, null),
                String.class
        );

        if (!getStudent.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("Get student falhou: status="
                        + getStudent.getStatusCode() + ", body=" + getStudent.getBody());
        }

        StudentResponse studentBody = readBody(getStudent, StudentResponse.class);
        assertThat(studentBody).isNotNull();
        assertThat(studentBody.email()).isEqualTo("joao.silva@email.com");
    }

    @Test
    void shouldCreateFlightAndPayment() {
        String token = registerAndLoginAdmin();

        Integer courseId = createCourse(token, "CPL");
        Integer studentId = createStudent(token, courseId, "ana.santos@email.com");
        Integer instructorId = createInstructor(token, "Cap. Ribeiro");
        Integer aircraftId = createAircraft(token, "CS-ABC");

        FlightRequest flightRequest = new FlightRequest(
                java.time.LocalDate.now(),
                java.time.LocalTime.of(10, 30),
                1.5,
                studentId,
                instructorId,
                aircraftId,
                "LPPT",
                "LPCS",
                "Local",
                "scheduled",
                "Treino circuito",
                null,
                null
        );

        ResponseEntity<FlightResponse> flightResponse = restTemplate.exchange(
                url("/bo/flights"),
                HttpMethod.POST,
                withAuth(token, flightRequest),
                FlightResponse.class
        );

        assertThat(flightResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(flightResponse.getBody()).isNotNull();
        assertThat(flightResponse.getBody().id()).isNotNull();

        PaymentRequest paymentRequest = new PaymentRequest(
                studentId,
                "Propina",
                1200.0,
                java.time.LocalDate.now().plusDays(30),
                null,
                "pending",
                "Transferencia",
                null
        );

        ResponseEntity<PaymentResponse> paymentResponse = restTemplate.exchange(
                url("/bo/payments"),
                HttpMethod.POST,
                withAuth(token, paymentRequest),
                PaymentResponse.class
        );

        assertThat(paymentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(paymentResponse.getBody()).isNotNull();
        assertThat(paymentResponse.getBody().id()).isNotNull();
    }

    @Test
    void shouldCreateEvaluationAndMaintenance() {
        String token = registerAndLoginAdmin();

        Integer courseId = createCourse(token, "IR");
        Integer studentId = createStudent(token, courseId, "maria.silva@email.com");
        Integer aircraftId = createAircraft(token, "CS-XYZ");

        EvaluationRequest evaluationRequest = new EvaluationRequest(
                studentId,
                courseId,
                "IR Teorico",
                java.time.LocalDate.now(),
                85,
                100,
                "passed",
                "theoretical",
                "Bom desempenho"
        );

        ResponseEntity<EvaluationResponse> evaluationResponse = restTemplate.exchange(
                url("/bo/evaluations"),
                HttpMethod.POST,
                withAuth(token, evaluationRequest),
                EvaluationResponse.class
        );

        assertThat(evaluationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(evaluationResponse.getBody()).isNotNull();
        assertThat(evaluationResponse.getBody().id()).isNotNull();

        MaintenanceRequest maintenanceRequest = new MaintenanceRequest(
                aircraftId,
                "Scheduled",
                "Revisao anual",
                "Tec Carlos",
                java.time.LocalDate.now(),
                java.time.LocalDate.now().plusDays(10),
                null,
                "scheduled",
                "medium",
                500.0,
                null
        );

        ResponseEntity<MaintenanceResponse> maintenanceResponse = restTemplate.exchange(
                url("/bo/maintenance"),
                HttpMethod.POST,
                withAuth(token, maintenanceRequest),
                MaintenanceResponse.class
        );

        assertThat(maintenanceResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(maintenanceResponse.getBody()).isNotNull();
        assertThat(maintenanceResponse.getBody().id()).isNotNull();
    }

    @Test
    void shouldListAllBackOfficeResources() {
        String token = registerAndLoginAdmin();

        String[] paths = {
                "/bo/courses",
                "/bo/students",
                "/bo/instructors",
                "/bo/aircraft",
                "/bo/flights",
                "/bo/payments",
                "/bo/evaluations",
                "/bo/maintenance",
                "/bo/reports/summary"
        };

        for (String path : paths) {
            ResponseEntity<String> response = restTemplate.exchange(
                    url(path),
                    HttpMethod.GET,
                    withAuth(token, null),
                    String.class
            );
            assertThat(response.getStatusCode()).as(path).isEqualTo(HttpStatus.OK);
            if (path.contains("/reports/")) {
                assertThat(response.getBody()).as(path).contains("totalStudents");
            } else {
                assertThat(response.getBody()).as(path).contains("content");
            }
        }
    }

    @Test
    void shouldRejectInvalidBusinessPayloads() {
        String token = registerAndLoginAdmin();
        Integer courseId = createCourse(token, "MEIR");

        StudentRequest invalidStudent = new StudentRequest(
                "Aluno Invalido",
                "aluno.invalido@email.com",
                "912000111",
                "123456789",
                java.time.LocalDate.now().minusYears(16),
                "Rua X",
                "Portugal",
                courseId,
                null,
                "active",
                null,
                50,
                1.0,
                1.0,
                "up_to_date"
        );
        ResponseEntity<String> invalidStudentResponse = restTemplate.exchange(
                url("/bo/students"),
                HttpMethod.POST,
                withAuth(token, invalidStudent),
                String.class
        );
        assertThat(invalidStudentResponse.getStatusCode().is4xxClientError()).isTrue();

        AircraftRequest invalidAircraft = new AircraftRequest(
                "CS-BAD",
                "Cessna 172",
                "Single Engine",
                2010,
                "flying",
                10.0,
                null,
                null,
                "Hangar A",
                101,
                null
        );
        ResponseEntity<String> invalidAircraftResponse = restTemplate.exchange(
                url("/bo/aircraft"),
                HttpMethod.POST,
                withAuth(token, invalidAircraft),
                String.class
        );
        assertThat(invalidAircraftResponse.getStatusCode().is4xxClientError()).isTrue();

        Integer studentId = createStudent(token, courseId, "pagamento.invalido@email.com");
        PaymentRequest invalidPayment = new PaymentRequest(
                studentId,
                "Propina",
                -1.0,
                java.time.LocalDate.now().plusDays(10),
                null,
                "pending",
                "Transferencia",
                null
        );
        ResponseEntity<String> invalidPaymentResponse = restTemplate.exchange(
                url("/bo/payments"),
                HttpMethod.POST,
                withAuth(token, invalidPayment),
                String.class
        );
        assertThat(invalidPaymentResponse.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void shouldUpdateAndDeleteCourse() {
        String token = registerAndLoginAdmin();
        Integer courseId = createCourse(token, "UPD");

        CourseRequest update = new CourseRequest(
                "UPD Advanced",
                "6 meses",
                20,
                30,
                4500.0,
                "Curso atualizado"
        );

        ResponseEntity<CourseResponse> updateResponse = restTemplate.exchange(
                url("/bo/courses/" + courseId),
                HttpMethod.PUT,
                withAuth(token, update),
                CourseResponse.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody()).isNotNull();
        assertThat(updateResponse.getBody().name()).isEqualTo("UPD Advanced");

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                url("/bo/courses/" + courseId),
                HttpMethod.DELETE,
                withAuth(token, null),
                Void.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> getDeleted = restTemplate.exchange(
                url("/bo/courses/" + courseId),
                HttpMethod.GET,
                withAuth(token, null),
                String.class
        );
        assertThat(getDeleted.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private String registerAndLoginAdmin() {
        registerAdmin();

        AuthLoginRequest login = new AuthLoginRequest("admin", "admin123");
        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity(
                url("/auth/login"),
                login,
                AuthResponse.class
        );

        if (loginResponse.getBody() == null) {
            throw new IllegalStateException("Token nao recebido");
        }
        return loginResponse.getBody().token();
    }

        private ResponseEntity<AuthResponse> registerAdmin() {
                AuthRegisterRequest register = new AuthRegisterRequest(
                                "admin",
                                "admin123",
                                "ADMIN",
                                null,
                                null
                );

                return restTemplate.postForEntity(
                                url("/auth/register"),
                                register,
                                AuthResponse.class
                );
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

        private Integer createCourse(String token, String name) {
                CourseRequest courseRequest = new CourseRequest(
                                name,
                                "12 meses",
                                45,
                                40,
                                8500.0,
                                "Curso " + name
                );

                ResponseEntity<String> courseResponse = restTemplate.exchange(
                                url("/bo/courses"),
                                HttpMethod.POST,
                                withAuth(token, courseRequest),
                                String.class
                );

                if (!courseResponse.getStatusCode().is2xxSuccessful()) {
                        throw new IllegalStateException("Course nao criado: status="
                                + courseResponse.getStatusCode() + ", body=" + courseResponse.getBody());
                }

                CourseResponse body = readBody(courseResponse, CourseResponse.class);
                if (body == null || body.id() == null) {
                        throw new IllegalStateException("Course nao criado: resposta invalida");
                }
                return body.id();
        }

        private Integer createStudent(String token, Integer courseId, String email) {
                String nif = String.format("%09d", Math.abs(email.hashCode()) % 1000000000);
        StudentRequest studentRequest = new StudentRequest(
                                "Ana Santos",
                                email,
                                "912000111",
                                null,
                                null,
                                "Rua B",
                                "Portugal",
                                courseId,
                                null,
                                "active",
                                null,
                                5,
                                2.0,
                                1.0,
                                "up_to_date"
                );

                ResponseEntity<String> studentResponse = restTemplate.exchange(
                                url("/bo/students"),
                                HttpMethod.POST,
                                withAuth(token, studentRequest),
                                String.class
                );

                if (!studentResponse.getStatusCode().is2xxSuccessful()) {
                        org.junit.jupiter.api.Assertions.fail("Student nao criado: status="
                                + studentResponse.getStatusCode() + ", body=" + studentResponse.getBody());
                }

                StudentResponse body = readBody(studentResponse, StudentResponse.class);
                if (body == null || body.id() == null) {
                        throw new IllegalStateException("Student nao criado: resposta invalida");
                }
                return body.id();
        }

        private Integer createInstructor(String token, String name) {
                InstructorRequest instructorRequest = new InstructorRequest(
                                name,
                                "CPL",
                                "PPL",
                                1200,
                                "active",
                                "instrutor@email.com",
                                "913000222"
                );

                ResponseEntity<String> response = restTemplate.exchange(
                                url("/bo/instructors"),
                                HttpMethod.POST,
                                withAuth(token, instructorRequest),
                                String.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new IllegalStateException("Instructor nao criado: status="
                                + response.getStatusCode() + ", body=" + response.getBody());
                }

                InstructorResponse body = readBody(response, InstructorResponse.class);
                if (body == null || body.id() == null) {
                        throw new IllegalStateException("Instructor nao criado: resposta invalida");
                }
                return body.id();
        }

        private Integer createAircraft(String token, String registration) {
                AircraftRequest aircraftRequest = new AircraftRequest(
                                registration,
                                "Cessna 172",
                                "Single Engine",
                                2010,
                                "operational",
                                1200.0,
                                null,
                                null,
                                "Hangar A",
                                80,
                                null
                );

                ResponseEntity<String> response = restTemplate.exchange(
                                url("/bo/aircraft"),
                                HttpMethod.POST,
                                withAuth(token, aircraftRequest),
                                String.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                        throw new IllegalStateException("Aircraft nao criado: status="
                                + response.getStatusCode() + ", body=" + response.getBody());
                }

                AircraftResponse body = readBody(response, AircraftResponse.class);
                if (body == null || body.id() == null) {
                        throw new IllegalStateException("Aircraft nao criado: resposta invalida");
                }
                return body.id();
        }

        private <T> T readBody(ResponseEntity<String> response, Class<T> type) {
                String body = response.getBody();
                if (body == null || body.isBlank()) {
                        return null;
                }
                try {
                        return objectMapper.readValue(body, type);
                } catch (JsonProcessingException ex) {
                        throw new IllegalStateException("Resposta invalida: " + body, ex);
                }
        }
}
