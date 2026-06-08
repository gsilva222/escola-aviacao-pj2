package pt.ipvc.estg.repositories;

import pt.ipvc.estg.entities.StudentDocument;

import java.util.List;
import java.util.Optional;

public interface StudentDocumentRepository {
    Optional<StudentDocument> findById(Integer id);
    Optional<StudentDocument> findByIdAndStudent(Integer id, Integer studentId);
    List<StudentDocument> findByStudentOrderByUploadedAtDesc(Integer studentId);
    StudentDocument save(StudentDocument document);
    void deleteById(Integer id);
}
