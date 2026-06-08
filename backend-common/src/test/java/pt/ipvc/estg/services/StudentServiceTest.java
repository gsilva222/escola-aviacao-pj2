package pt.ipvc.estg.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.dal.mock.MockDalReset;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.repositories.CourseRepository;
import pt.ipvc.estg.repositories.InstructorRepository;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.repositories.mock.MockCourseRepository;
import pt.ipvc.estg.repositories.mock.MockInstructorRepository;
import pt.ipvc.estg.repositories.mock.MockStudentRepository;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentService studentService;
    private Course course;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        StudentRepository studentRepository = new MockStudentRepository();
        CourseRepository courseRepository = new MockCourseRepository();
        InstructorRepository instructorRepository = new MockInstructorRepository();
        studentService = new StudentService(studentRepository, courseRepository, instructorRepository);
        course = courseRepository.save(new Course("PPL", "12 meses", 45, 100, 25000.0));
    }

    @Test
    void createEstudante_persistsStudent() {
        Student student = new Student("Ana Silva", "ana@test.com", course);
        Student created = studentService.createEstudante(student);
        assertNotNull(created.getId());
        assertEquals("Ana Silva", created.getName());
    }

    @Test
    void createEstudante_rejectsDuplicateEmail() {
        studentService.createEstudante(new Student("Ana Silva", "dup@test.com", course));
        assertThrows(ConflictException.class,
                () -> studentService.createEstudante(new Student("Outra", "dup@test.com", course)));
    }

    @Test
    void updateProfile_updatesContactFields() {
        Student created = studentService.createEstudante(new Student("Joao", "joao@test.com", course));
        Student updated = studentService.updateProfile(created.getId(), "912345678", "Rua A", "Portuguesa");
        assertEquals("912345678", updated.getPhone());
        assertEquals("Rua A", updated.getAddress());
        assertEquals("Portuguesa", updated.getNationality());
    }
}
