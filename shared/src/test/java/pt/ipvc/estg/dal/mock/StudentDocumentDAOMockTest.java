package pt.ipvc.estg.dal.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.StudentDocument;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentDocumentDAOMockTest {

    private StudentDocumentDAOMock dao;
    private Student student;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        dao = new StudentDocumentDAOMock();
        Course course = new CourseDAOMock().insert(new Course("PPL", "12 meses", 45, 100, 8500.0));
        student = new StudentDAOMock().insert(new Student("Ana", "ana@test.com", course));
    }

    @Test
    void findByStudentOrderByUploadedAtDesc_sortsNewestFirst() {
        StudentDocument older = new StudentDocument(student, "old.pdf", "application/pdf", "/tmp/old.pdf", "ID");
        older.setUploadedAt(LocalDateTime.now().minusDays(2));
        dao.insert(older);

        StudentDocument newer = new StudentDocument(student, "new.pdf", "application/pdf", "/tmp/new.pdf", "ID");
        newer.setUploadedAt(LocalDateTime.now());
        dao.insert(newer);

        List<StudentDocument> docs = dao.findByStudentOrderByUploadedAtDesc(student.getId());
        assertEquals(2, docs.size());
        assertEquals("new.pdf", docs.get(0).getFileName());
    }

    @Test
    void findByIdAndStudent_filtersByOwner() {
        StudentDocument doc = dao.insert(new StudentDocument(student, "doc.pdf", "application/pdf", "/tmp/doc.pdf", "ID"));
        assertTrue(dao.findByIdAndStudent(doc.getId(), student.getId()).isPresent());
        assertTrue(dao.findByIdAndStudent(doc.getId(), 999).isEmpty());
    }
}
