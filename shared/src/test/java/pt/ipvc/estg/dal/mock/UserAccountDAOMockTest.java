package pt.ipvc.estg.dal.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.UserAccount;

import static org.junit.jupiter.api.Assertions.*;

class UserAccountDAOMockTest {

    private UserAccountDAOMock dao;
    private Student student;

    @BeforeEach
    void setUp() {
        MockDalReset.resetAll();
        dao = new UserAccountDAOMock();
        Course course = new CourseDAOMock().insert(new Course("PPL", "12 meses", 45, 100, 8500.0));
        student = new StudentDAOMock().insert(new Student("Ana", "ana@test.com", course));
    }

    @Test
    void findByUsername_isCaseInsensitive() {
        dao.insert(new UserAccount("Admin", "hash", "ADMIN"));
        assertTrue(dao.findByUsername("admin").isPresent());
    }

    @Test
    void findByStudent_returnsLinkedAccount() {
        UserAccount account = new UserAccount("ana@test.com", "hash", "STUDENT");
        account.setStudent(student);
        dao.insert(account);

        assertTrue(dao.findByStudent(student.getId()).isPresent());
    }
}
