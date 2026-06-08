package pt.ipvc.estg.dal.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;

import static org.junit.jupiter.api.Assertions.*;

class StudentDAOMockTest {

    private StudentDAOMock dao;
    private Course course;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        dao = new StudentDAOMock();
        course = new CourseDAOMock().insert(new Course("PPL", "12 meses", 45, 100, 8500.0));
    }

    @Test
    void findByEmail_isCaseInsensitive() {
        dao.insert(new Student("Ana", "Ana@Test.com", course));
        assertTrue(dao.findByEmail("ana@test.com").isPresent());
    }

    @Test
    void findByCourse_returnsStudentsForCourse() {
        dao.insert(new Student("Ana", "ana@test.com", course));
        dao.insert(new Student("Joao", "joao@test.com", course));
        assertEquals(2, dao.findByCourse(course.getId()).size());
    }
}
