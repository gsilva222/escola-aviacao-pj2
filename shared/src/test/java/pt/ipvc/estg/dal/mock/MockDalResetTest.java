package pt.ipvc.estg.dal.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.entities.UserAccount;

import static org.junit.jupiter.api.Assertions.*;

class MockDalResetTest {

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
    }

    @Test
    void resetAll_clearsAllDaos() {
        Course course = new CourseDAOMock().insert(new Course("PPL", "12 meses", 45, 100, 8500.0));
        Student student = new StudentDAOMock().insert(new Student("Ana", "ana@test.com", course));
        new UserAccountDAOMock().insert(new UserAccount("admin", "hash", "ADMIN"));
        new StudentDocumentDAOMock().insert(
                new StudentDocument(student, "doc.pdf", "application/pdf", "/tmp/doc.pdf", "ID"));

        MockDalReset.resetAll();

        assertEquals(0, new StudentDAOMock().count());
        assertEquals(0, new CourseDAOMock().count());
        assertEquals(0, new UserAccountDAOMock().count());
        assertEquals(0, new StudentDocumentDAOMock().count());
        assertEquals(0, new PerfilDAOMock().count());
    }
}
