package pt.ipvc.estg.web.services;

import org.springframework.stereotype.Service;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.StudentService;
import pt.ipvc.estg.web.security.SecurityUtils;

@Service
public class StudentScopeService {

    private final StudentService studentService;

    public StudentScopeService(StudentService studentService) {
        this.studentService = studentService;
    }

    public Student requireCurrentStudent() {
        int studentId = SecurityUtils.requireStudentId();
        return studentService.requireEstudante(studentId);
    }

    public Student requireStudent(Integer studentId) {
        return studentService.requireEstudante(studentId);
    }
}
