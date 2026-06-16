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
import pt.ipvc.estg.web.dto.AircraftRequest;
import pt.ipvc.estg.web.dto.AircraftResponse;
import pt.ipvc.estg.web.dto.CourseRequest;
import pt.ipvc.estg.web.dto.CourseResponse;
import pt.ipvc.estg.web.dto.FlightRequest;
import pt.ipvc.estg.web.dto.InstructorRequest;
import pt.ipvc.estg.web.dto.InstructorResponse;
import pt.ipvc.estg.web.dto.StudentRequest;
import pt.ipvc.estg.web.dto.StudentResponse;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FlightInstructorConflictIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRejectCreatingFlightWithSameInstructorAtSameTime() {
        String suffix = String.valueOf(System.nanoTime());
        String token = registerAndLoginAdmin(suffix);

        Integer courseId = createCourse(token, "FLIGHT_COURSE_" + suffix);

        Integer student1Id = createStudent(token, courseId, "s1");
        Integer student2Id = createStudent(token, courseId, "s2");

        Integer instructorId = createInstructor(token, "Capt. Instrutor_" + suffix, suffix);

        Integer aircraft1Id = createAircraft(token, "CS-INSTR-1");
        Integer aircraft2Id = createAircraft(token, "CS-INSTR-2");

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 30);
        double duration = 1.5;

        FlightRequest flight1 = new FlightRequest(
                date,
                time,
                duration,
                student1Id,
                instructorId,
                aircraft1Id,
                "LPPT",
                "LPCS",
                "Local",
                "scheduled",
                "Instrucao 1",
                null,
                null
        );

        ResponseEntity<String> created1 = restTemplate.exchange(
                url("/bo/flights"),
                HttpMethod.POST,
                withAuth(token, flight1),
                String.class
        );
        assertThat(created1.getStatusCode()).isEqualTo(HttpStatus.OK);

        FlightRequest flight2 = new FlightRequest(
                date,
                time,
                duration,
                student2Id,
                instructorId,
                aircraft2Id,
                "LPPT",
                "LPCS",
                "Local",
                "scheduled",
                "Instrucao 2",
                null,
                null
        );

        ResponseEntity<String> conflict = restTemplate.exchange(
                url("/bo/flights"),
                HttpMethod.POST,
                withAuth(token, flight2),
                String.class
        );

        assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(conflict.getBody()).contains("instrutor ja possui voo nesse intervalo");
    }

    private String registerAndLoginAdmin(String suffix) {
        String username = "admin.instr." + suffix;
        restTemplate.postForEntity(
                url("/auth/register"),
                new pt.ipvc.estg.web.dto.AuthRegisterRequest(username, "admin123", "ADMIN", null, null),
                String.class
        );

        ResponseEntity<pt.ipvc.estg.web.dto.AuthResponse> login = restTemplate.postForEntity(
                url("/auth/login"),
                new pt.ipvc.estg.web.dto.AuthLoginRequest(username, "admin123"),
                pt.ipvc.estg.web.dto.AuthResponse.class
        );
        return login.getBody().token();
    }

    private Integer createCourse(String token, String name) {
        CourseRequest req = new CourseRequest(
                name,
                "12 meses",
                45,
                40,
                8500.0,
                "Curso " + name
        );
        ResponseEntity<CourseResponse> resp = restTemplate.exchange(
                url("/bo/courses"),
                HttpMethod.POST,
                withAuth(token, req),
                CourseResponse.class
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return resp.getBody().id();
    }

    private Integer createStudent(String token, Integer courseId, String testSuffix) {
        String suffix = testSuffix + "_" + System.nanoTime();
        String email = "aluno.instr." + suffix + "@email.com";
        String nif = String.valueOf(Math.abs(suffix.hashCode()) % 1_000_000_000);
        nif = (nif + "000000000").substring(0, 9);

        StudentRequest req = new StudentRequest(
                "Aluno Instrutor",
                email,
                "912000111",
                null,
                null,
                "Rua Instrutor",
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

        ResponseEntity<StudentResponse> resp = restTemplate.exchange(
                url("/bo/students"),
                HttpMethod.POST,
                withAuth(token, req),
                StudentResponse.class
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return resp.getBody().id();
    }

    private Integer createInstructor(String token, String name, String suffix) {
        InstructorRequest req = new InstructorRequest(
                name,
                "CPL",
                "PPL",
                1200,
                "active",
                "instrutor." + suffix + "@email.com",
                "913000222" + (suffix.length() > 0 ? suffix.charAt(suffix.length() - 1) : '0')
        );

        ResponseEntity<InstructorResponse> resp = restTemplate.exchange(
                url("/bo/instructors"),
                HttpMethod.POST,
                withAuth(token, req),
                InstructorResponse.class
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return resp.getBody().id();
    }

    private Integer createAircraft(String token, String registration) {
        AircraftRequest req = new AircraftRequest(
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
        ResponseEntity<AircraftResponse> resp = restTemplate.exchange(
                url("/bo/aircraft"),
                HttpMethod.POST,
                withAuth(token, req),
                AircraftResponse.class
        );
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        return resp.getBody().id();
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

