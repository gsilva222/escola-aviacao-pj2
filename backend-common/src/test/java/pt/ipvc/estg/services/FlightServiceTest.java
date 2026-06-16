package pt.ipvc.estg.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.dal.mock.MockDalReset;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.mock.MockAircraftRepository;
import pt.ipvc.estg.repositories.mock.MockCourseRepository;
import pt.ipvc.estg.repositories.mock.MockFlightRepository;
import pt.ipvc.estg.repositories.mock.MockInstructorRepository;
import pt.ipvc.estg.repositories.mock.MockMaintenanceRepository;
import pt.ipvc.estg.repositories.mock.MockStudentRepository;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FlightServiceTest {

    private FlightService flightService;
    private Student student;
    private Student otherStudent;
    private Instructor instructor;
    private Instructor otherInstructor;
    private Aircraft aircraft;
    private Aircraft otherAircraft;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        MockStudentRepository studentRepository = new MockStudentRepository();
        MockInstructorRepository instructorRepository = new MockInstructorRepository();
        MockAircraftRepository aircraftRepository = new MockAircraftRepository();
        MockCourseRepository courseRepository = new MockCourseRepository();

        flightService = new FlightService(
                new MockFlightRepository(),
                studentRepository,
                instructorRepository,
                aircraftRepository,
                new MockMaintenanceRepository()
        );

        Course course = courseRepository.save(new Course("PPL", "12 meses", 45, 100, 25000.0));
        student = studentRepository.save(new Student("Aluno A", "a@test.com", course));
        otherStudent = studentRepository.save(new Student("Aluno B", "b@test.com", course));

        instructor = instructorRepository.save(buildInstructor("Instrutor A", "FI-1"));
        otherInstructor = instructorRepository.save(buildInstructor("Instrutor B", "FI-2"));

        aircraft = aircraftRepository.save(buildAircraft("CS-AAA"));
        otherAircraft = aircraftRepository.save(buildAircraft("CS-BBB"));

        date = LocalDate.now().plusDays(5);
    }

    @Test
    void criarVoo_allowsNonOverlappingSlots() {
        flightService.criarVoo(date, LocalTime.of(10, 0), 1.0, student, instructor, aircraft,
                "LPPT", "LPPT", "training");

        assertDoesNotThrow(() -> flightService.criarVoo(
                date, LocalTime.of(11, 0), 1.0, otherStudent, otherInstructor, otherAircraft,
                "LPPT", "LPPT", "training"));
    }

    @Test
    void criarVoo_rejectsSameAircraftOverlap() {
        flightService.criarVoo(date, LocalTime.of(10, 0), 1.5, student, instructor, aircraft,
                "LPPT", "LPPT", "training");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                flightService.criarVoo(date, LocalTime.of(11, 0), 1.0, otherStudent, otherInstructor, aircraft,
                        "LPPT", "LPPT", "training"));
        assert ex.getMessage().contains("aeronave");
    }

    @Test
    void criarVoo_rejectsSameStudentOverlap() {
        flightService.criarVoo(date, LocalTime.of(10, 0), 1.5, student, instructor, aircraft,
                "LPPT", "LPPT", "training");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                flightService.criarVoo(date, LocalTime.of(11, 0), 1.0, student, otherInstructor, otherAircraft,
                        "LPPT", "LPPT", "training"));
        assert ex.getMessage().contains("aluno");
    }

    @Test
    void criarVoo_rejectsSameInstructorOverlap() {
        flightService.criarVoo(date, LocalTime.of(10, 0), 1.5, student, instructor, aircraft,
                "LPPT", "LPPT", "training");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                flightService.criarVoo(date, LocalTime.of(11, 0), 1.0, otherStudent, instructor, otherAircraft,
                        "LPPT", "LPPT", "training"));
        assert ex.getMessage().contains("instrutor");
    }

    @Test
    void criarVoo_rejectsWhenExistingLessonHasNotFinished() {
        flightService.criarVoo(date, LocalTime.of(10, 0), 2.0, student, instructor, aircraft,
                "LPPT", "LPPT", "training");

        assertThrows(IllegalArgumentException.class, () ->
                flightService.criarVoo(date, LocalTime.of(11, 0), 1.0, otherStudent, otherInstructor, aircraft,
                        "LPPT", "LPPT", "training"));
    }

    @Test
    void criarVoo_persistsFlightWithScheduleSlot() {
        var created = flightService.criarVoo(date, LocalTime.of(14, 30), 1.5, student, instructor, aircraft,
                "LPPT", "LPPT", "training");
        assertNotNull(created.getId());
    }

    @Test
    void criarVoo_rejectsPastDate() {
        assertThrows(IllegalArgumentException.class, () ->
                flightService.criarVoo(LocalDate.now().minusDays(1), LocalTime.of(10, 0), 1.0,
                        student, instructor, aircraft, "LPPT", "LPPT", "training"));
    }

    private static Instructor buildInstructor(String name, String license) {
        Instructor instructor = new Instructor();
        instructor.setName(name);
        instructor.setLicense(license);
        instructor.setStatus("active");
        return instructor;
    }

    private static Aircraft buildAircraft(String registration) {
        Aircraft aircraft = new Aircraft();
        aircraft.setRegistration(registration);
        aircraft.setModel("C172");
        aircraft.setType("Single Engine");
        aircraft.setStatus("operational");
        return aircraft;
    }
}
