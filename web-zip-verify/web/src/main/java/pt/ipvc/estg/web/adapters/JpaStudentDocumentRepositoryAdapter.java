package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.entities.StudentDocument;
import pt.ipvc.estg.repositories.StudentDocumentRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaStudentDocumentRepositoryAdapter implements StudentDocumentRepository {

    private final pt.ipvc.estg.web.repositories.StudentDocumentRepository jpa;

    public JpaStudentDocumentRepositoryAdapter(pt.ipvc.estg.web.repositories.StudentDocumentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<StudentDocument> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<StudentDocument> findByIdAndStudent(Integer id, Integer studentId) {
        return jpa.findByIdAndStudent_Id(id, studentId);
    }

    @Override
    public List<StudentDocument> findByStudentOrderByUploadedAtDesc(Integer studentId) {
        return jpa.findByStudent_IdOrderByUploadedAtDesc(studentId);
    }

    @Override
    public StudentDocument save(StudentDocument document) {
        return jpa.save(document);
    }

    @Override
    public void deleteById(Integer id) {
        jpa.deleteById(id);
    }
}
