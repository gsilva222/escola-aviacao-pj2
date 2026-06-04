package pt.ipvc.estg.web.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.security.SecurityUtils;

@Service
public class StudentScopeService {

    private final StudentRepository studentRepository;

    public StudentScopeService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student requireCurrentStudent() {
        int studentId = SecurityUtils.requireStudentId();
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
    }

    public Student requireStudent(Integer studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
    }
}
